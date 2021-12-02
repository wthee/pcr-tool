package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.UniqueEquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.skill.SkillCompose
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_EQUIPMENT
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.getFormatText
import cn.wthee.pcrtool.utils.int
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import com.google.accompanist.insets.LocalWindowInsets
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * 角色信息
 *
 * @param unitId 角色编号
 */
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun CharacterDetail(
    scrollState: ScrollState,
    unitId: Int,
    actions: NavActions,
    attrViewModel: CharacterAttrViewModel = hiltViewModel(),
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    //最大值
    val maxValue = attrViewModel.getMaxRankAndRarity(unitId)
        .collectAsState(initial = CharacterProperty()).value
    val currentValueState = navViewModel.currentValue.observeAsState()
    //选择的 RANK
    val selectRank = navViewModel.selectRank.observeAsState().value ?: 0
    //数值信息
    if (currentValueState.value == null && maxValue.isInit()) {
        navViewModel.currentValue.postValue(maxValue)
        if (selectRank == 0) {
            navViewModel.selectRank.postValue(maxValue.rank)
        }
    }
    // bottomsheet 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    if (!state.isVisible && !state.isAnimationRunning) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabCloseClick.postValue(false)
    }
    //关闭监听
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    //收藏状态
    val filter = navViewModel.filterCharacter.observeAsState()
    val loved = remember {
        mutableStateOf(filter.value?.starIds?.contains(unitId) ?: false)
    }
    //技能循环
    val loopData =
        skillViewModel.getCharacterSkillLoops(unitId).collectAsState(initial = arrayListOf()).value
    val iconTypes = skillViewModel.iconTypes.observeAsState().value ?: hashMapOf()
    //角色属性
    val allData = attrViewModel.getCharacterInfo(unitId, currentValueState.value)
        .collectAsState(initial = AllAttrData()).value
    //角色特殊六星id
    val cutinId = attrViewModel.getCutinId(unitId).collectAsState(initial = 0).value

    //输入框管理
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val insets = LocalWindowInsets.current
    val context = LocalContext.current

    //战力系数
    val coe = attrViewModel.getCoefficient().collectAsState(initial = null).value


    //页面
    ModalBottomSheetLayout(
        sheetState = state,
        scrimColor = colorResource(id = if (isSystemInDarkTheme()) R.color.alpha_black else R.color.alpha_white),
        sheetElevation = Dimen.sheetElevation,
        sheetShape = if (state.offset.value == 0f) {
            noShape
        } else {
            Shape.large
        },
        sheetContent = {
            SkillLoopList(
                loopData,
                iconTypes,
                Modifier.padding(
                    top = Dimen.largePadding,
                    start = Dimen.mediumPadding,
                    end = Dimen.mediumPadding,
                ),
                unitType = 0
            )
        },
        sheetBackgroundColor = MaterialTheme.colorScheme.surface
    ) {
        currentValueState.value?.let { currentValue ->
            val unknown = maxValue.level == -1
            //角色等级滑动条
            val characterLevel = remember {
                mutableStateOf(currentValue.level)
            }
            //专武等级滑动条
            val uniqueEquipLevel = remember {
                mutableStateOf(currentValue.uniqueEquipmentLevel)
            }
            //Rank 选择
            if (selectRank != 0 && selectRank != currentValue.rank) {
                navViewModel.currentValue.postValue(currentValue.update(rank = selectRank))
            }

            //关闭
            if (close) {
                coroutineScope.launch {
                    state.hide()
                }
                navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                navViewModel.fabCloseClick.postValue(false)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                //数据加载后，展示页面
                val visible = allData.sumAttr.hp > 1 && allData.equips.isNotEmpty()
                FadeAnimation(visible) {
                    //页面
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //角色卡面
                        CharacterCard(loved.value, actions, unitId)
                        Column(
                            modifier = Modifier.width(getItemWidth()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //战力计算
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val value = if (coe == null) {
                                    ""
                                } else {
                                    val basicAttr =
                                        allData.sumAttr.copy().sub(allData.exSkillAttr)
                                    val basic = basicAttr.hp * coe.hp_coefficient +
                                            basicAttr.atk * coe.atk_coefficient +
                                            basicAttr.magicStr * coe.magic_str_coefficient +
                                            basicAttr.def * coe.def_coefficient +
                                            basicAttr.magicDef * coe.magic_def_coefficient +
                                            basicAttr.physicalCritical * coe.physical_critical_coefficient +
                                            basicAttr.magicCritical * coe.magic_critical_coefficient +
                                            basicAttr.waveHpRecovery * coe.wave_hp_recovery_coefficient +
                                            basicAttr.waveEnergyRecovery * coe.wave_energy_recovery_coefficient +
                                            basicAttr.dodge * coe.dodge_coefficient +
                                            basicAttr.physicalPenetrate * coe.physical_penetrate_coefficient +
                                            basicAttr.magicPenetrate * coe.magic_penetrate_coefficient +
                                            basicAttr.lifeSteal * coe.life_steal_coefficient +
                                            basicAttr.hpRecoveryRate * coe.hp_recovery_rate_coefficient +
                                            basicAttr.energyRecoveryRate * coe.energy_recovery_rate_coefficient +
                                            basicAttr.energyReduceRate * coe.energy_reduce_rate_coefficient +
                                            basicAttr.accuracy * coe.accuracy_coefficient
                                    //技能2：默认加上技能2
                                    var skill = currentValue.level * coe.skill_lv_coefficient
                                    //技能1：解锁专武，技能1系数提升
                                    if (allData.uniqueEquip.equipmentId != UNKNOWN_EQUIP_ID) {
                                        skill += coe.skill1_evolution_coefficient
                                        skill += currentValue.level * coe.skill_lv_coefficient * coe.skill1_evolution_slv_coefficient
                                    } else {
                                        skill += currentValue.level * coe.skill_lv_coefficient
                                    }
                                    //不同星级处理
                                    if (currentValue.rarity >= 5) {
                                        //ex+:大于等于五星，技能 ex+
                                        skill += coe.exskill_evolution_coefficient
                                        skill += currentValue.level * coe.skill_lv_coefficient
                                        if (currentValue.rarity == 6) {
                                            //ub+
                                            skill += coe.ub_evolution_coefficient
                                            skill += currentValue.level * coe.skill_lv_coefficient * coe.ub_evolution_slv_coefficient
                                        } else {
                                            //ub
                                            skill += currentValue.level * coe.skill_lv_coefficient
                                        }
                                    } else {
                                        //ub、ex
                                        skill += currentValue.level * coe.skill_lv_coefficient * 2
                                    }
                                    (basic + skill).int.toString()
                                }
                                MainText(
                                    text = stringResource(R.string.attr_all_value) + value,
                                    modifier = Modifier
                                        .padding(end = Dimen.smallPadding)
                                        .animateContentSize(defaultSpring())
                                )
                                IconCompose(
                                    data = MainIconType.HELP.icon,
                                    size = Dimen.smallIconSize
                                ) {
                                    actions.toCoe()
                                }
                            }
                            //星级
                            StarSelect(
                                currentValue = currentValue,
                                max = maxValue.rarity,
                                modifier = Modifier.padding(top = Dimen.mediumPadding)
                            )

                            //等级
                            Text(
                                text = characterLevel.value.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(Dimen.smallPadding)
                                    .fillMaxWidth(0.3f)
                                    .padding(Dimen.mediumPadding)
                                    .clip(Shape.small)
                                    .clickable {
                                        VibrateUtil(context).single()
                                        if (insets.ime.isVisible) {
                                            focusManager.clearFocus()
                                            keyboardController?.hide()
                                        } else {
                                            focusRequester.requestFocus()
                                            keyboardController?.show()
                                        }
                                    }
                            )
                            //等级输入框
                            val inputLevel = remember {
                                mutableStateOf("")
                            }
                            OutlinedTextField(
                                value = inputLevel.value,
                                colors = outlinedTextFieldColors(),
                                onValueChange = {
                                    var filterStr = ""
                                    it.forEach { ch ->
                                        if (Regex("\\d").matches(ch.toString())) {
                                            filterStr += ch
                                        }
                                    }
                                    inputLevel.value = when {
                                        filterStr == "" -> ""
                                        filterStr.toInt() < 1 -> "1"
                                        filterStr.toInt() in 1..maxValue.level -> filterStr
                                        else -> maxValue.level.toString()
                                    }
                                },
                                textStyle = MaterialTheme.typography.bodyMedium,
                                trailingIcon = {
                                    IconCompose(
                                        data = MainIconType.OK.icon,
                                        size = Dimen.fabIconSize
                                    ) {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        if (inputLevel.value != "") {
                                            characterLevel.value = inputLevel.value.toInt()
                                        }
                                        navViewModel.currentValue.postValue(
                                            currentValue.update(
                                                level = characterLevel.value
                                            )
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Number
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        if (inputLevel.value != "") {
                                            characterLevel.value = inputLevel.value.toInt()
                                        }
                                        navViewModel.currentValue.postValue(
                                            currentValue.update(
                                                level = characterLevel.value
                                            )
                                        )
                                    }
                                ),
                                modifier = if (insets.ime.isVisible) {
                                    Modifier
                                        .focusRequester(focusRequester)
                                        .padding(Dimen.smallPadding)
                                } else {
                                    Modifier
                                        .focusRequester(focusRequester)
                                        .height(1.dp)
                                        .alpha(0f)
                                }
                            )
                        }
                        //属性
                        AttrLists(allData)
                        //RANK 装备
                        CharacterEquip(
                            unitId = unitId,
                            rank = currentValue.rank,
                            maxRank = maxValue.rank,
                            equips = allData.equips,
                            toEquipDetail = actions.toEquipDetail,
                            toRankEquip = actions.toCharacteRankEquip,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        //显示专武
                        if (allData.uniqueEquip.equipmentId != UNKNOWN_EQUIP_ID) {
                            UniqueEquip(
                                currentValue = currentValue,
                                uniqueEquipLevelMax = maxValue.uniqueEquipmentLevel,
                                uniqueEquipLevel = uniqueEquipLevel,
                                uniqueEquipmentMaxData = allData.uniqueEquip
                            )
                        }
                        //技能
                        SkillCompose(
                            unitId = unitId,
                            cutinId = cutinId,
                            level = currentValue.level,
                            atk = max(
                                allData.sumAttr.atk.int,
                                allData.sumAttr.magicStr.int
                            ),
                            unitType = 0,
                            toSummonDetail = actions.toSummonDetail
                        )
                        CommonSpacer()
                        Spacer(modifier = Modifier.height(Dimen.fabSize + Dimen.fabMargin))
                    }
                }
                if (unknown) {
                    Column(
                        modifier = Modifier
                            .padding(Dimen.largePadding)
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CharacterCard(loved.value, actions, unitId)
                        //未知角色占位页面
                        Text(
                            text = stringResource(R.string.unknown_character),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(Dimen.largePadding)
                        )
                    }

                }
                //悬浮按钮
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier.padding(
                            bottom = if (unknown) Dimen.fabMargin else Dimen.fabSmallMarginEnd,
                            end = Dimen.fabMargin
                        )
                    ) {
                        //收藏
                        FabCompose(
                            iconType = if (loved.value) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
                            hasNavBarPadding = unknown
                        ) {
                            filter.value?.addOrRemove(unitId)
                            loved.value = !loved.value
                        }
                        //跳转至角色资料
                        FabCompose(
                            iconType = MainIconType.CHARACTER_INTRO,
                            hasNavBarPadding = unknown
                        ) {
                            actions.toCharacterBasicInfo(unitId)
                        }
                        //技能循环
                        FabCompose(
                            iconType = MainIconType.SKILL_LOOP,
                            hasNavBarPadding = unknown,
                            modifier = Modifier.alpha(if (unknown) 0f else 1f)
                        ) {
                            coroutineScope.launch {
                                if (state.isVisible) {
                                    navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                                    state.hide()
                                } else {
                                    navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                                    state.show()
                                }
                            }
                        }
                    }
                    if (!unknown) {
                        Row(
                            modifier = Modifier
                                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                        ) {
                            //跳转至 RANK 对比页面
                            FabCompose(
                                iconType = MainIconType.RANK_COMPARE,
                                text = stringResource(id = R.string.compare),
                            ) {
                                actions.toCharacteRankCompare(
                                    unitId,
                                    maxValue.rank,
                                    currentValue.level,
                                    currentValue.rarity,
                                    currentValue.uniqueEquipmentLevel,
                                )
                            }
                            //跳转至装备统计页面
                            FabCompose(
                                iconType = MainIconType.EQUIP_CALC,
                                text = stringResource(id = R.string.count),
                            ) {
                                actions.toCharacteEquipCount(unitId, maxValue.rank)
                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun CharacterCard(
    loved: Boolean,
    actions: NavActions,
    unitId: Int,
    characterViewModel: CharacterViewModel = hiltViewModel()
) {
    //基本信息
    val basicInfo =
        characterViewModel.getCharacterBasicInfo(unitId).collectAsState(initial = null).value
    if (basicInfo != null) {
        CharacterItem(
            basicInfo,
            loved,
            modifier = Modifier
                .padding(Dimen.largePadding)
                .width(getItemWidth()),
            numberStyle = MaterialTheme.typography.bodyMedium,
            size = Dimen.fabIconSize
        ) {
            actions.toCharacterPics(unitId)
        }
    }
}

/**
 * 属性
 */
@ExperimentalComposeUiApi
@Composable
private fun AttrLists(allData: AllAttrData) {
    //属性
    AttrList(attrs = allData.sumAttr.all())
    //剧情属性
    MainText(
        text = stringResource(id = R.string.title_story_attr),
        modifier = Modifier.padding(
            top = Dimen.largePadding,
            bottom = Dimen.smallPadding
        )
    )
    AttrList(attrs = allData.storyAttr.allNotZero())
    //Rank 奖励
    val hasBonus = allData.bonus.attr.allNotZero().isNotEmpty()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (hasBonus) {
            MainText(
                text = stringResource(id = R.string.title_rank_bonus),
                modifier = Modifier.Companion
                    .padding(
                        top = Dimen.largePadding,
                        bottom = Dimen.smallPadding
                    ),
                textAlign = TextAlign.Center
            )
            AttrList(attrs = allData.bonus.attr.allNotZero())
        }
    }
}

/**
 * 角色 RANK 装备
 * @param unitId 角色编号
 * @param rank 当前rank
 * @param equips 装备列表
 */
@Composable
private fun CharacterEquip(
    unitId: Int,
    rank: Int,
    maxRank: Int,
    equips: List<EquipmentMaxData>,
    toEquipDetail: (Int) -> Unit,
    toRankEquip: (Int) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxWidth(0.8f)) {
        //装备 6、 3
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.largePadding)
        ) {
            val id6 = equips[0].equipmentId
            val id3 = equips[1].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getUrl(ICON_EQUIPMENT, id6)) {
                if (id6 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id6)
                }
            }
            IconCompose(data = ImageResourceHelper.getInstance().getUrl(ICON_EQUIPMENT, id3)) {
                toEquipDetail(id3)
            }
        }
        //装备 5、 2
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.mediumPadding)
        ) {
            val id5 = equips[2].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getUrl(ICON_EQUIPMENT, id5)) {
                toEquipDetail(id5)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = MainIconType.BACK.icon,
                    contentDescription = null,
                    tint = if (rank < maxRank) {
                        getRankColor(rank = rank + 1)
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    modifier = Modifier
                        .size(Dimen.mediumIconSize)
                        .clip(Shape.medium)
                        .clickable(enabled = rank < maxRank) {
                            VibrateUtil(context).single()
                            navViewModel.selectRank.postValue(rank + 1)
                        }
                )
                //跳转至所有 RANK 装备列表
                SubButton(
                    text = getFormatText(rank),
                    color = getRankColor(rank),
                    modifier = Modifier.padding(
                        top = Dimen.largePadding * 2,
                        bottom = Dimen.largePadding * 2,
                    )
                ) {
                    toRankEquip(unitId)
                }
                Icon(
                    imageVector = MainIconType.MORE.icon,
                    contentDescription = null,
                    tint = if (rank > 1) {
                        getRankColor(rank = rank - 1)
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    modifier = Modifier
                        .size(Dimen.mediumIconSize)
                        .clip(Shape.medium)
                        .clickable(enabled = rank > 1) {
                            VibrateUtil(context).single()
                            navViewModel.selectRank.postValue(rank - 1)
                        }
                )
            }
            val id2 = equips[3].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getUrl(ICON_EQUIPMENT, id2)) {
                toEquipDetail(id2)
            }

        }
        //装备 4、 1
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.mediumPadding, bottom = Dimen.mediumPadding)
        ) {
            val id4 = equips[4].equipmentId
            val id1 = equips[5].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getUrl(ICON_EQUIPMENT, id4)) {
                toEquipDetail(id4)
            }
            IconCompose(data = ImageResourceHelper.getInstance().getUrl(ICON_EQUIPMENT, id1)) {
                toEquipDetail(id1)
            }
        }
    }
}

/**
 * 专武信息
 * @param uniqueEquipLevelMax 最大等级
 * @param uniqueEquipLevel 等级状态
 * @param uniqueEquipmentMaxData 专武数值信息
 */
@ExperimentalComposeUiApi
@Composable
private fun UniqueEquip(
    currentValue: CharacterProperty,
    uniqueEquipLevelMax: Int,
    uniqueEquipLevel: MutableState<Int>,
    uniqueEquipmentMaxData: UniqueEquipmentMaxData?,
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val insets = LocalWindowInsets.current
    val context = LocalContext.current

    uniqueEquipmentMaxData?.let {
        Column(
            modifier = Modifier.padding(top = Dimen.largePadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //名称
            MainText(
                text = it.equipmentName,
                selectable = true
            )
            //专武等级
            Text(
                text = uniqueEquipLevel.value.toString(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = Dimen.smallPadding)
                    .fillMaxWidth(0.3f)
                    .padding(Dimen.mediumPadding)
                    .clip(Shape.small)
                    .clickable {
                        VibrateUtil(context).single()
                        if (insets.ime.isVisible) {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        } else {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }
                    }
            )
            //等级输入框
            val inputLevel = remember {
                mutableStateOf("")
            }
            OutlinedTextField(
                value = inputLevel.value,
                shape = Shape.medium,
                colors = outlinedTextFieldColors(),
                onValueChange = {
                    var filterStr = ""
                    it.forEach { ch ->
                        if (Regex("\\d").matches(ch.toString())) {
                            filterStr += ch
                        }
                    }
                    inputLevel.value = when {
                        filterStr == "" -> ""
                        filterStr.toInt() < 1 -> "1"
                        filterStr.toInt() in 1..uniqueEquipLevelMax -> filterStr
                        else -> uniqueEquipLevelMax.toString()
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                trailingIcon = {
                    IconCompose(
                        data = MainIconType.OK.icon,
                        size = Dimen.fabIconSize
                    ) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (inputLevel.value != "") {
                            uniqueEquipLevel.value = inputLevel.value.toInt()
                        }
                        navViewModel.currentValue.postValue(
                            currentValue.update(
                                uniqueEquipmentLevel = uniqueEquipLevel.value
                            )
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (inputLevel.value != "") {
                            uniqueEquipLevel.value = inputLevel.value.toInt()
                        }
                        navViewModel.currentValue.postValue(
                            currentValue.update(
                                uniqueEquipmentLevel = uniqueEquipLevel.value
                            )
                        )

                    }
                ),
                modifier = if (insets.ime.isVisible) {
                    Modifier
                        .focusRequester(focusRequester)
                        .padding(Dimen.smallPadding)
                } else {
                    Modifier
                        .focusRequester(focusRequester)
                        .height(1.dp)
                        .alpha(0f)
                }
            )
            //图标描述
            Row(
                modifier = Modifier
                    .padding(
                        start = Dimen.largePadding,
                        end = Dimen.largePadding,
                        bottom = Dimen.mediumPadding
                    )
                    .fillMaxWidth()
            ) {
                IconCompose(
                    data = ImageResourceHelper.getInstance().getUrl(ICON_EQUIPMENT, it.equipmentId)
                )
                Subtitle2(
                    text = it.getDesc(),
                    modifier = Modifier.padding(start = Dimen.mediumPadding),
                    selectable = true
                )
            }
            //属性
            AttrList(attrs = it.attr.allNotZero())
        }
    }

}

/**
 * 星级选择
 * @param max 最大值
 */
@Composable
private fun StarSelect(
    currentValue: CharacterProperty,
    max: Int,
    modifier: Modifier = Modifier
) {

    Row(modifier) {
        for (i in 1..max) {
            val iconId = when {
                i > currentValue.rarity -> R.drawable.ic_star_dark
                i == 6 -> R.drawable.ic_star_pink
                else -> R.drawable.ic_star
            }
            IconCompose(
                data = iconId,
                size = Dimen.mediumIconSize,
                modifier = Modifier.padding(Dimen.smallPadding)
            ) {
                navViewModel.currentValue.postValue(currentValue.update(rarity = i))
            }

        }
    }
}