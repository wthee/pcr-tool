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
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.skill.SkillCompose
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * 角色信息
 *
 * @param unitId 角色编号
 */
@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
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
    //数值信息
    if ((currentValueState.value == null || currentValueState.value!!.level == -1) && maxValue.isInit()) {
        navViewModel.currentValue.postValue(maxValue)
    }
    // bottomsheet 状态
    val modalBottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    if (!modalBottomSheetState.isVisible && !modalBottomSheetState.isAnimationRunning) {
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
    val characterAttrData = attrViewModel.getCharacterInfo(unitId, currentValueState.value)
        .collectAsState(initial = AllAttrData()).value
    //角色特殊六星id
    val cutinId = attrViewModel.getCutinId(unitId).collectAsState(initial = 0).value

    //输入框管理
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isImeVisible = WindowInsets.isImeVisible
    val context = LocalContext.current

    //战力系数
    val coe = attrViewModel.getCoefficient().collectAsState(initial = null).value


    //页面
    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        scrimColor = colorResource(id = if (isSystemInDarkTheme()) R.color.alpha_black else R.color.alpha_white),
        sheetElevation = Dimen.sheetElevation,
        sheetShape = if (modalBottomSheetState.offset.value == 0f) {
            noShape
        } else {
            Shape.large
        },
        sheetContent = {
            //技能循环
            SkillLoopList(
                loopData,
                iconTypes,
                Modifier.padding(Dimen.largePadding),
                unitType = UnitType.CHARACTER
            )
        },
        sheetBackgroundColor = MaterialTheme.colorScheme.surface
    ) {
        currentValueState.value?.let { currentValue ->
            if (maxValue.isInit() && currentValue.rarity > maxValue.rarity) {
                navViewModel.currentValue.postValue(currentValue.update(rarity = 5))
            }
            val unknown = maxValue.level == -1
            //关闭
            if (close) {
                coroutineScope.launch {
                    modalBottomSheetState.hide()
                }
                navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                navViewModel.fabCloseClick.postValue(false)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                //数据加载后，展示页面
                val visible =
                    characterAttrData.sumAttr.hp > 1 && characterAttrData.equips.isNotEmpty()
                //页面
                FadeAnimation(visible) {
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
                                        characterAttrData.sumAttr.copy()
                                            .sub(characterAttrData.exSkillAttr)
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
                                    if (characterAttrData.uniqueEquip.equipmentId != UNKNOWN_EQUIP_ID) {
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
                                    data = MainIconType.HELP,
                                    size = Dimen.smallIconSize
                                ) {
                                    actions.toCoe()
                                }
                            }
                            //星级
                            StarSelect(
                                currentValue = currentValue,
                                max = maxValue.rarity,
                                modifier = Modifier.padding(top = Dimen.mediumPadding),
                            )

                            //等级
                            Text(
                                text = currentValue.level.toString(),
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
                                        if (isImeVisible) {
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
                                        data = MainIconType.OK,
                                        size = Dimen.fabIconSize
                                    ) {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        if (inputLevel.value != "") {
                                            navViewModel.currentValue.postValue(
                                                currentValue.update(
                                                    level = inputLevel.value.toInt()
                                                )
                                            )
                                        }
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
                                            navViewModel.currentValue.postValue(
                                                currentValue.update(
                                                    level = inputLevel.value.toInt()
                                                )
                                            )
                                        }
                                    }
                                ),
                                modifier = if (isImeVisible) {
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
                        AttrLists(unitId, characterAttrData, actions)
                        //RANK相关功能
                        Row(
                            modifier = Modifier
                                .padding(Dimen.largePadding)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            //RANK对比
                            IconTextButton(
                                icon = MainIconType.RANK_COMPARE,
                                text = stringResource(id = R.string.rank_compare),
                                iconSize = Dimen.fabIconSize,
                                textStyle = MaterialTheme.typography.bodyMedium
                            ) {
                                actions.toCharacteRankCompare(
                                    unitId,
                                    maxValue.rank,
                                    currentValue.level,
                                    currentValue.rarity,
                                    currentValue.uniqueEquipmentLevel,
                                )
                            }
                            //装备统计
                            IconTextButton(
                                icon = MainIconType.EQUIP_CALC,
                                text = stringResource(id = R.string.calc_equip_count),
                                iconSize = Dimen.fabIconSize,
                                textStyle = MaterialTheme.typography.bodyMedium
                            ) {
                                actions.toCharacteEquipCount(unitId, maxValue.rank)
                            }
                        }
                        //RANK 装备
                        CharacterEquip(
                            unitId = unitId,
                            rank = currentValue.rank,
                            maxRank = maxValue.rank,
                            equips = characterAttrData.equips,
                            toEquipDetail = actions.toEquipDetail,
                            toRankEquip = actions.toCharacteRankEquip,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        //显示专武
                        if (characterAttrData.uniqueEquip.equipmentId != UNKNOWN_EQUIP_ID) {
                            UniqueEquip(
                                currentValue = currentValue,
                                uniqueEquipLevelMax = maxValue.uniqueEquipmentLevel,
                                uniqueEquipmentMaxData = characterAttrData.uniqueEquip,
                            )
                        }
                        //技能
                        SkillCompose(
                            unitId = unitId,
                            cutinId = cutinId,
                            level = currentValue.level,
                            atk = max(
                                characterAttrData.sumAttr.atk.int,
                                characterAttrData.sumAttr.magicStr.int
                            ),
                            unitType = UnitType.CHARACTER,
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
                                if (modalBottomSheetState.isVisible) {
                                    navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                                    modalBottomSheetState.hide()
                                } else {
                                    navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                                    modalBottomSheetState.show()
                                }
                            }
                        }
                    }
                    if (!unknown) {
                        Row(
                            modifier = Modifier
                                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                        ) {
                            //预览模型
                            FabCompose(
                                iconType = MainIconType.PREVIEW_UNIT_SPINE,
                                text = stringResource(R.string.model_preview)
                            ) {
                                BrowserUtil.open(context, Constants.PREVIEW_UNIT_URL + unitId)
                            }
                        }
                    }
                }
            }
        }
    }
}

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
            character = basicInfo,
            loved = loved,
            modifier = Modifier
                .padding(Dimen.largePadding)
                .width(getItemWidth())
        ) {
            actions.toAllPics(unitId, AllPicsType.CHARACTER.type)
        }
    }
}

/**
 * 属性
 */
@Composable
private fun AttrLists(unitId: Int, allData: AllAttrData, actions: NavActions) {
    //属性
    AttrList(attrs = allData.sumAttr.all())
    //剧情属性
    Row(
        modifier = Modifier.padding(
            top = Dimen.largePadding,
            bottom = Dimen.smallPadding
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainText(
            text = stringResource(id = R.string.title_story_attr),
            modifier = Modifier.padding(end = Dimen.smallPadding)
        )
        IconCompose(
            data = MainIconType.HELP,
            size = Dimen.smallIconSize
        ) {
            actions.toCharacteStoryDetail(unitId)
        }
    }

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

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //装备 6、 3
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(Dimen.iconSize * 4)
        ) {
            val id6 = equips[0].equipmentId
            val id3 = equips[1].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id6)) {
                if (id6 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id6)
                }
            }
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id3)) {
                toEquipDetail(id3)
            }
        }
        //装备 5、 2
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.mediumPadding)
        ) {
            val id5 = equips[2].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id5)) {
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
                        .padding(start = Dimen.mediumPadding)
                        .size(Dimen.mediumIconSize)
                        .clip(Shape.medium)
                        .clickable(enabled = rank < maxRank) {
                            VibrateUtil(context).single()
                            val value = navViewModel.currentValue.value
                            navViewModel.currentValue.postValue(value?.update(rank = rank + 1))
                        }
                )
                //跳转至所有 RANK 装备列表
                SubButton(
                    text = getFormatText(rank),
                    color = getRankColor(rank),
                    modifier = Modifier.padding(
                        vertical = Dimen.largePadding * 2
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
                        .padding(end = Dimen.mediumPadding)
                        .size(Dimen.mediumIconSize)
                        .clip(Shape.medium)
                        .clickable(enabled = rank > 1) {
                            VibrateUtil(context).single()
                            val value = navViewModel.currentValue.value
                            navViewModel.currentValue.postValue(value?.update(rank = rank - 1))
                        }
                )
            }
            val id2 = equips[3].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id2)) {
                toEquipDetail(id2)
            }

        }
        //装备 4、 1
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = Dimen.mediumPadding, bottom = Dimen.mediumPadding)
                .width(Dimen.iconSize * 4)
        ) {
            val id4 = equips[4].equipmentId
            val id1 = equips[5].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id4)) {
                toEquipDetail(id4)
            }
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id1)) {
                toEquipDetail(id1)
            }
        }
    }
}

/**
 * 专武信息
 * @param currentValue 当前属性
 * @param uniqueEquipLevelMax 等级
 * @param uniqueEquipmentMaxData 专武数值信息
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun UniqueEquip(
    currentValue: CharacterProperty,
    uniqueEquipLevelMax: Int,
    uniqueEquipmentMaxData: UniqueEquipmentMaxData?,
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isImeVisible = WindowInsets.isImeVisible
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
                text = currentValue.uniqueEquipmentLevel.toString(),
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
                        if (isImeVisible) {
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
                        data = MainIconType.OK,
                        size = Dimen.fabIconSize
                    ) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (inputLevel.value != "") {
                            navViewModel.currentValue.postValue(
                                currentValue.update(
                                    uniqueEquipmentLevel = inputLevel.value.toInt()
                                )
                            )
                        }
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
                            navViewModel.currentValue.postValue(
                                currentValue.update(
                                    uniqueEquipmentLevel = inputLevel.value.toInt()
                                )
                            )
                        }
                    }
                ),
                modifier = if (isImeVisible) {
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
                    data = ImageResourceHelper.getInstance().getEquipPic(it.equipmentId)
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
    modifier: Modifier = Modifier,
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