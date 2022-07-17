package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import cn.wthee.pcrtool.ui.skill.SummonDetail
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ShapeTop
import cn.wthee.pcrtool.ui.theme.colorAlphaBlack
import cn.wthee.pcrtool.ui.theme.colorAlphaWhite
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.max

private enum class SheetType {
    EMPTY,

    /**
     *  技能循环
     */
    SKILL_LOOP,

    /**
     * RANK 对比
     */
    RANK_COMPARE,

    /**
     * 装备统计
     */
    RANK_EQUIP_COUNT,

    /**
     * 角色基本信息
     */
    BASIC_INFO,

    /**
     * 角色RANK装备选择
     */
    RANK_EQUIP_SELECT,

    /**
     * 角色技能召唤物信息
     */
    SKILL_SUMMON_DETAIL,

    /**
     * 角色立绘
     */
    ALL_CARD_IMAGE,

    /**
     * 角色剧情属性详情
     */
    STORY_ATTR,

    /**
     * 角色战力详情
     */
    ATTR_COE,
}

/**
 * 角色信息
 *
 * @param unitId 角色编号
 */
@OptIn(
    ExperimentalMaterialApi::class
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
    //当前选择的数值信息
    val currentValueState = remember {
        mutableStateOf(CharacterProperty())
    }
    //数值信息
    if (currentValueState.value.level == 0 && maxValue.isInit()) {
        //初始为最大值
        currentValueState.value = maxValue
    }
    // bottom sheet 状态
    val modalBottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val sheetTypeState = remember {
        mutableStateOf(SheetType.EMPTY)
    }
    if (!modalBottomSheetState.isVisible && !modalBottomSheetState.isAnimationRunning) {
        sheetTypeState.value = SheetType.EMPTY
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabCloseClick.postValue(false)
    }
    val openSheet = {
        navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
        coroutineScope.launch {
            modalBottomSheetState.show()
        }
    }
    //关闭监听
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    //bottom sheet 关闭
    if (close || sheetTypeState.value == SheetType.EMPTY) {
        LaunchedEffect(modalBottomSheetState.targetValue) {
            //关闭
            modalBottomSheetState.hide()
        }
    }
    //角色特殊六星id
    val cutinId = attrViewModel.getCutinId(unitId).collectAsState(initial = 0).value
    //技能循环
    val loopData =
        skillViewModel.getCharacterSkillLoops(unitId).collectAsState(initial = arrayListOf()).value
    val iconTypes = skillViewModel.getskillIconTypes(unitId, cutinId)
        .collectAsState(initial = hashMapOf()).value
    //角色属性
    val characterAttrData = attrViewModel.getCharacterInfo(unitId, currentValueState.value)
        .collectAsState(initial = AllAttrData()).value

    //数据加载后，展示页面
    val visible = characterAttrData.sumAttr.hp > 1 && characterAttrData.equips.isNotEmpty()
    //未实装角色
    val unknown = maxValue.level == -1
    //技能召唤物id
    val summonId = remember {
        mutableStateOf(0)
    }

    //页面
    ModalBottomSheetLayout(sheetState = modalBottomSheetState,
        scrimColor = if (isSystemInDarkTheme()) colorAlphaBlack else colorAlphaWhite,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        sheetShape = ShapeTop(),
        sheetContent = {
            when (sheetTypeState.value) {
                SheetType.EMPTY -> {
                    Spacer(modifier = Modifier.height(Dimen.divLineHeight))
                }
                SheetType.SKILL_LOOP -> {
                    //技能循环
                    SkillLoopList(
                        loopData,
                        iconTypes,
                        modifier = Modifier.padding(Dimen.largePadding),
                        unitType = UnitType.CHARACTER
                    )
                }
                SheetType.RANK_COMPARE -> {
                    RankCompare(
                        unitId, maxValue.rank, currentValue = currentValueState.value
                    )
                }
                SheetType.RANK_EQUIP_COUNT -> {
                    RankEquipCount(
                        unitId = unitId,
                        maxRank = maxValue.rank,
                        toEquipMaterial = actions.toEquipMaterial,
                        navViewModel = navViewModel
                    )
                }
                SheetType.BASIC_INFO -> {
                    CharacterBasicInfo(unitId)
                }
                SheetType.RANK_EQUIP_SELECT -> {
                    RankEquipList(
                        unitId = unitId,
                        currentValueState = currentValueState,
                        toEquipDetail = actions.toEquipDetail
                    )
                }
                SheetType.SKILL_SUMMON_DETAIL -> {
                    SummonDetail(
                        unitId = summonId.value,
                        unitType = UnitType.CHARACTER_SUMMON,
                        currentValueState = currentValueState
                    )
                }
                SheetType.ALL_CARD_IMAGE -> {
                    AllCardList(
                        id = unitId,
                        allPicsType = AllPicsType.CHARACTER
                    )
                }
                SheetType.STORY_ATTR -> {
                    CharacterStoryDetail(unitId = unitId)
                }
                SheetType.ATTR_COE -> {
                    CharacterStatusCoeCompose()
                }
            }
        }) {
        Box(modifier = Modifier.fillMaxSize()) {
            //页面
            if (visible) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //角色卡面
                    CharacterCard(
                        unitId,
                        characterAttrData,
                        currentValueState.value,
                        sheetTypeState,
                        openSheet
                    )
                    //星级
                    StarSelect(
                        currentValueState = currentValueState, max = maxValue.rarity
                    )
                    //角色等级
                    CharacterLevel(currentValueState = currentValueState, maxValue.level)
                    //属性
                    AttrLists(
                        characterAttrData,
                        sheetTypeState,
                        openSheet
                    )
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
                            sheetTypeState.value = SheetType.RANK_COMPARE
                            openSheet()
                        }
                        //装备统计
                        IconTextButton(
                            icon = MainIconType.EQUIP_CALC,
                            text = stringResource(id = R.string.calc_equip_count),
                            iconSize = Dimen.fabIconSize,
                            textStyle = MaterialTheme.typography.bodyMedium
                        ) {
                            sheetTypeState.value = SheetType.RANK_EQUIP_COUNT
                            openSheet()
                        }
                    }
                    //RANK 装备
                    CharacterEquip(
                        currentValueState = currentValueState,
                        maxRank = maxValue.rank,
                        equips = characterAttrData.equips,
                        toEquipDetail = actions.toEquipDetail,
                        sheetType = sheetTypeState,
                        openSheet = openSheet
                    )
                    //显示专武
                    if (characterAttrData.uniqueEquip.equipmentId != UNKNOWN_EQUIP_ID) {
                        UniqueEquip(
                            currentValueState = currentValueState,
                            uniqueEquipLevelMax = maxValue.uniqueEquipmentLevel,
                            uniqueEquipmentMaxData = characterAttrData.uniqueEquip,
                        )
                    }
                    //技能列表
                    SkillCompose(unitId = unitId,
                        cutinId = cutinId,
                        level = currentValueState.value.level,
                        atk = max(
                            characterAttrData.sumAttr.atk.int,
                            characterAttrData.sumAttr.magicStr.int
                        ),
                        unitType = UnitType.CHARACTER,
                        toSummonDetail = { sid, _ ->
                            summonId.value = sid
                            sheetTypeState.value = SheetType.SKILL_SUMMON_DETAIL
                            openSheet()
                        })
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
            if (!unknown) {
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier.padding(
                            end = Dimen.fabMarginEnd,
                            bottom = Dimen.fabMargin
                        )
                    ) {
                        //技能循环
                        FabCompose(
                            iconType = MainIconType.SKILL_LOOP,
                        ) {
                            sheetTypeState.value = SheetType.SKILL_LOOP
                            openSheet()
                        }
                    }
                }
            }
        }
    }
}

/**
 * 角色卡
 */
@Composable
private fun CharacterCard(
    unitId: Int,
    characterAttrData: AllAttrData,
    currentValue: CharacterProperty,
    sheetTypeState: MutableState<SheetType>,
    openSheet: () -> Job,
    characterViewModel: CharacterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    //收藏状态
    val filter = navViewModel.filterCharacter.observeAsState()
    val loved = remember {
        mutableStateOf(filter.value?.starIds?.contains(unitId) ?: false)
    }


    //基本信息
    val basicInfo =
        characterViewModel.getCharacterBasicInfo(unitId).collectAsState(initial = null).value
    if (basicInfo != null) {
        Column(
            modifier = Modifier
                .padding(Dimen.largePadding)
                .width(getItemWidth()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //卡面信息
            Box {
                CharacterItem(
                    character = basicInfo, loved = loved.value
                ) {
                    filter.value?.addOrRemove(unitId)
                    loved.value = !loved.value
                }
            }

            //其它功能
            Row(
                modifier = Modifier
                    .padding(top = Dimen.smallPadding)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //战力计算
                CharacterCoe(characterAttrData, currentValue, sheetTypeState, openSheet)
                Spacer(modifier = Modifier.weight(1f))
                //资料
                IconTextButton(
                    icon = MainIconType.CHARACTER_INTRO,
                    text = stringResource(id = R.string.character_basic_info),
                ) {
                    sheetTypeState.value = SheetType.BASIC_INFO
                    openSheet()
                }
                //立绘预览
                IconTextButton(
                    icon = MainIconType.PREVIEW_IMAGE,
                    text = stringResource(id = R.string.character_pic),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                ) {
                    sheetTypeState.value = SheetType.ALL_CARD_IMAGE
                    openSheet()
                }
                //模型预览
                IconTextButton(
                    icon = MainIconType.PREVIEW_UNIT_SPINE,
                    text = stringResource(id = R.string.model_preview),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                ) {
                    BrowserUtil.open(context, Constants.PREVIEW_UNIT_URL + unitId)
                }
            }
        }
    }
}

/**
 * 战力计算
 */
@Composable
private fun CharacterCoe(
    characterAttrData: AllAttrData,
    currentValue: CharacterProperty,
    sheetTypeState: MutableState<SheetType>,
    openSheet: () -> Job,
    attrViewModel: CharacterAttrViewModel = hiltViewModel()
) {
    //战力系数
    val coe = attrViewModel.getCoefficient().collectAsState(initial = null).value
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val value = if (coe == null) {
            ""
        } else {
            val basicAttr = characterAttrData.sumAttr.copy().sub(characterAttrData.exSkillAttr)
            val basic =
                basicAttr.hp * coe.hp_coefficient + basicAttr.atk * coe.atk_coefficient + basicAttr.magicStr * coe.magic_str_coefficient + basicAttr.def * coe.def_coefficient + basicAttr.magicDef * coe.magic_def_coefficient + basicAttr.physicalCritical * coe.physical_critical_coefficient + basicAttr.magicCritical * coe.magic_critical_coefficient + basicAttr.waveHpRecovery * coe.wave_hp_recovery_coefficient + basicAttr.waveEnergyRecovery * coe.wave_energy_recovery_coefficient + basicAttr.dodge * coe.dodge_coefficient + basicAttr.physicalPenetrate * coe.physical_penetrate_coefficient + basicAttr.magicPenetrate * coe.magic_penetrate_coefficient + basicAttr.lifeSteal * coe.life_steal_coefficient + basicAttr.hpRecoveryRate * coe.hp_recovery_rate_coefficient + basicAttr.energyRecoveryRate * coe.energy_recovery_rate_coefficient + basicAttr.energyReduceRate * coe.energy_reduce_rate_coefficient + basicAttr.accuracy * coe.accuracy_coefficient
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
            text = stringResource(id = R.string.attr_all_value, value),
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable {
                    VibrateUtil(context).single()
                    sheetTypeState.value = SheetType.ATTR_COE
                    openSheet()
                }
                .padding(horizontal = Dimen.smallPadding)
        )
    }
}

/**
 * 角色等级
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
private fun CharacterLevel(
    currentValueState: MutableState<CharacterProperty>, maxLevel: Int
) {
    val context = LocalContext.current

    //角色等级
    val inputLevel = remember {
        mutableStateOf("")
    }
    //输入框管理
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isImeVisible = WindowInsets.isImeVisible

    //等级
    Text(text = currentValueState.value.level.toString(),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(Dimen.smallPadding)
            .fillMaxWidth(0.3f)
            .padding(Dimen.mediumPadding)
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                VibrateUtil(context).single()
                if (isImeVisible) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                } else {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            })
    //等级输入框
    OutlinedTextField(value = inputLevel.value, onValueChange = {
        var filterStr = ""
        it.forEach { ch ->
            if (Regex("\\d").matches(ch.toString())) {
                filterStr += ch
            }
        }
        inputLevel.value = when {
            filterStr == "" -> ""
            filterStr.toInt() < 1 -> "1"
            filterStr.toInt() in 1..maxLevel -> filterStr
            else -> maxLevel.toString()
        }
    }, textStyle = MaterialTheme.typography.bodyMedium, trailingIcon = {
        IconCompose(
            data = MainIconType.OK, size = Dimen.fabIconSize
        ) {
            keyboardController?.hide()
            focusManager.clearFocus()
            if (inputLevel.value != "") {
                currentValueState.value = currentValueState.value.update(
                    level = inputLevel.value.toInt()
                )
            }
        }
    }, keyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
    ), keyboardActions = KeyboardActions(onDone = {
        keyboardController?.hide()
        focusManager.clearFocus()
        if (inputLevel.value != "") {
            currentValueState.value = currentValueState.value.update(
                level = inputLevel.value.toInt()
            )
        }
    }), modifier = if (isImeVisible) {
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

/**
 * 属性
 */
@Composable
private fun AttrLists(
    allData: AllAttrData,
    sheetTypeState: MutableState<SheetType>,
    openSheet: () -> Job
) {
    val hasBonus = allData.bonus.attr.allNotZero().isNotEmpty()
    val context = LocalContext.current

    //属性
    AttrList(attrs = allData.sumAttr.all())
    //剧情属性
    if (allData.storyAttr.allNotZero().isNotEmpty()) {
        Row(
            modifier = Modifier.padding(
                top = Dimen.largePadding, bottom = Dimen.smallPadding
            ), verticalAlignment = Alignment.CenterVertically
        ) {
            MainText(
                text = stringResource(id = R.string.title_story_attr),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable {
                        VibrateUtil(context).single()
                        sheetTypeState.value = SheetType.STORY_ATTR
                        openSheet()
                    }
                    .padding(horizontal = Dimen.smallPadding)

            )
        }
        AttrList(attrs = allData.storyAttr.allNotZero())
    }
    //Rank 奖励
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (hasBonus) {
            MainText(
                text = stringResource(id = R.string.title_rank_bonus),
                modifier = Modifier.Companion.padding(
                    top = Dimen.largePadding, bottom = Dimen.smallPadding
                ),
                textAlign = TextAlign.Center
            )
            AttrList(attrs = allData.bonus.attr.allNotZero())
        }
    }
}

/**
 * 角色 RANK 装备
 */
@Composable
private fun CharacterEquip(
    currentValueState: MutableState<CharacterProperty>,
    maxRank: Int,
    equips: List<EquipmentMaxData>,
    toEquipDetail: (Int) -> Unit,
    sheetType: MutableState<SheetType>,
    openSheet: () -> Job
) {
    val rank = currentValueState.value.rank

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //装备 6、 3
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(Dimen.iconSize * 4)
        ) {
            val id6 = equips[0].equipmentId
            val id3 = equips[1].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id6)) {
                if (id6 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id6)
                }
            }
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id3)) {
                if (id3 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id3)
                }
            }
        }
        //装备 5、 2
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.mediumPadding)
        ) {
            val id5 = equips[2].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id5)) {
                if (id5 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id5)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconCompose(data = MainIconType.BACK,
                    tint = if (currentValueState.value.rank < maxRank) {
                        getRankColor(rank = rank + 1)
                    } else {
                        Color.Transparent
                    },
                    onClick = if (rank < maxRank) {
                        {
                            currentValueState.value = currentValueState.value.update(
                                rank = rank + 1
                            )
                        }
                    } else {
                        null
                    },
                    size = Dimen.mediumIconSize,
                    modifier = Modifier.padding(start = Dimen.mediumPadding))
                //跳转至所有 RANK 装备列表
                SubButton(
                    text = getFormatText(rank),
                    color = getRankColor(rank),
                    modifier = Modifier.padding(
                        vertical = Dimen.largePadding * 2
                    )
                ) {
                    sheetType.value = SheetType.RANK_EQUIP_SELECT
                    openSheet()
                }
                IconCompose(data = MainIconType.MORE,
                    tint = if (rank > 1) {
                        getRankColor(rank = rank - 1)
                    } else {
                        Color.Transparent
                    },
                    onClick = if (rank > 1) {
                        {
                            currentValueState.value = currentValueState.value.update(
                                rank = rank - 1
                            )
                        }
                    } else {
                        null
                    },
                    size = Dimen.mediumIconSize,
                    modifier = Modifier.padding(end = Dimen.mediumPadding))
            }
            val id2 = equips[3].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id2)) {
                if (id2 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id2)
                }
            }

        }
        //装备 4、 1
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(Dimen.iconSize * 4)
        ) {
            val id4 = equips[4].equipmentId
            val id1 = equips[5].equipmentId
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id4)) {
                if (id4 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id4)
                }

            }
            IconCompose(data = ImageResourceHelper.getInstance().getEquipPic(id1)) {
                if (id1 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id1)
                }
            }
        }
    }
}

/**
 * 专武信息
 * @param currentValueState 当前属性
 * @param uniqueEquipLevelMax 等级
 * @param uniqueEquipmentMaxData 专武数值信息
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun UniqueEquip(
    currentValueState: MutableState<CharacterProperty>,
    uniqueEquipLevelMax: Int,
    uniqueEquipmentMaxData: UniqueEquipmentMaxData?,
) {
    val inputLevel = remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isImeVisible = WindowInsets.isImeVisible
    val context = LocalContext.current

    uniqueEquipmentMaxData?.let {
        Column(
            modifier = Modifier.padding(top = Dimen.largePadding * 2),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //名称
            MainText(
                text = it.equipmentName, selectable = true
            )
            //专武等级
            Text(text = currentValueState.value.uniqueEquipmentLevel.toString(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = Dimen.smallPadding)
                    .fillMaxWidth(0.3f)
                    .padding(Dimen.mediumPadding)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        VibrateUtil(context).single()
                        if (isImeVisible) {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        } else {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }
                    })
            //等级输入框

            OutlinedTextField(
                value = inputLevel.value,
                shape = MaterialTheme.shapes.medium,
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
                        data = MainIconType.OK, size = Dimen.fabIconSize
                    ) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (inputLevel.value != "") {
                            currentValueState.value = currentValueState.value.update(
                                uniqueEquipmentLevel = inputLevel.value.toInt()
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    if (inputLevel.value != "") {
                        currentValueState.value = currentValueState.value.update(
                            uniqueEquipmentLevel = inputLevel.value.toInt()
                        )
                    }
                }),
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
    currentValueState: MutableState<CharacterProperty>,
    max: Int,
    modifier: Modifier = Modifier,
) {

    Row(modifier) {
        for (i in 1..max) {
            val iconId = when {
                i > currentValueState.value.rarity -> R.drawable.ic_star_dark
                i == 6 -> R.drawable.ic_star_pink
                else -> R.drawable.ic_star
            }
            IconCompose(
                data = iconId,
                size = Dimen.fabIconSize,
                modifier = Modifier.padding(Dimen.smallPadding)
            ) {
                currentValueState.value = currentValueState.value.update(
                    rarity = i
                )
            }

        }
    }
}