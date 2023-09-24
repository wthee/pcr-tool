package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.UnitPromotionBonus
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.CharacterDetailModuleType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.SkillType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.components.AttrList
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainHorizontalPagerIndicator
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.SubButton
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.components.getRankColor
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.skill.SkillLayout
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.defaultTween
import cn.wthee.pcrtool.ui.tool.uniqueequip.UniqueEquip
import cn.wthee.pcrtool.ui.tool.uniqueequip.UnitIconAndTag
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.getFormatText
import cn.wthee.pcrtool.utils.int
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import kotlin.math.max


private const val defaultOrder = "300-301-302-303-304-305-306-307-308-310-"

/**
 * 角色信息
 *
 * @param unitId 角色编号
 * @param showDetailState 非空时从专用装备跳转
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterDetail(
    unitId: Int,
    actions: NavActions,
    showDetailState: MutableState<Boolean>? = null,
    attrViewModel: CharacterAttrViewModel = hiltViewModel(),
    characterViewModel: CharacterViewModel = hiltViewModel(),
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    val sp = mainSP()

    //是否显示全部信息
    val showDetail = showDetailState == null || showDetailState.value

    //特殊形态角色id（吉塔）
    val cutinIdFlow = remember {
        attrViewModel.getCutinId(unitId)
    }
    val cutinId by cutinIdFlow.collectAsState(initial = 0)
    //形态切换
    val isCutinSkill = remember {
        mutableStateOf(true)
    }
    //不同技能形态对应的 unitId
    val currentIdState = remember {
        mutableIntStateOf(0)
    }
    currentIdState.intValue = if (isCutinSkill.value && cutinId != 0) {
        cutinId
    } else {
        unitId
    }

    //最大值
    val maxValueFlow = remember {
        attrViewModel.getMaxRankAndRarity(unitId)
    }
    val maxValue by maxValueFlow.collectAsState(initial = CharacterProperty())
    //当前选择的数值信息
    val currentValueState = remember {
        mutableStateOf(CharacterProperty())
    }
    //数值信息
    if (currentValueState.value.level == 0 && maxValue.isInit()) {
        //初始为最大值
        currentValueState.value = maxValue
    }

    //rank 装备选择监听
    val rankEquipSelected = navViewModel.rankEquipSelected.observeAsState().value ?: 0
    LaunchedEffect(rankEquipSelected) {
        if (rankEquipSelected != 0 && currentValueState.value.rank != rankEquipSelected) {
            currentValueState.value = currentValueState.value.update(rank = rankEquipSelected)
        }
    }

    //角色属性
    val characterAttrFlow = remember(currentValueState.value.hashCode()) {
        attrViewModel.getCharacterInfo(unitId, currentValueState.value)
    }
    val characterAttrData by characterAttrFlow.collectAsState(initial = AllAttrData())

    //基本信息
    val basicInfoFlow = remember {
        characterViewModel.getCharacterBasicInfo(unitId)
    }
    val basicInfo by basicInfoFlow.collectAsState(initial = CharacterInfo())

    //角色攻击力
    val atk = max(
        characterAttrData.sumAttr.atk.int,
        characterAttrData.sumAttr.magicStr.int
    )

    //普通技能
    val normalSkillFlow = remember(isCutinSkill.value, currentValueState.value.level, atk) {
        skillViewModel.getCharacterSkills(
            currentValueState.value.level,
            atk,
            currentIdState.intValue,
            SkillType.NORMAL
        )
    }
    val normalSkillData by normalSkillFlow.collectAsState(initial = arrayListOf())

    //sp技能
    val spSkillFlow = remember(isCutinSkill.value, currentValueState.value.level, atk) {
        skillViewModel.getCharacterSkills(
            currentValueState.value.level,
            atk,
            currentIdState.intValue,
            SkillType.SP
        )
    }
    val spSkillData by spSkillFlow.collectAsState(initial = arrayListOf())

    // sp技能标签
    val spLabelFlow = remember(isCutinSkill.value) {
        skillViewModel.getSpSkillLabel(currentIdState.intValue)
    }
    val spLabel by spLabelFlow.collectAsState(initial = null)

    //数据加载后，展示页面
    val visible = characterAttrData.sumAttr.hp > 1 && characterAttrData.equips.isNotEmpty()
    //未实装角色
    val unknown = maxValue.level == -1

    //收藏状态
    val starIds = FilterCharacter.getStarIdList()
    val loved = remember {
        mutableStateOf(starIds.contains(unitId))
    }

    //编辑模式
    var isEditMode by remember {
        mutableStateOf(false)
    }

    //自定义显示顺序
    val localData = sp.getString(Constants.SP_CHARACTER_DETAIL_ORDER, defaultOrder) ?: ""
    var orderData = navViewModel.characterDetailOrderData.observeAsState().value ?: ""
    LaunchedEffect(orderData, showDetail) {
        orderData = if (showDetail) {
            localData
        } else {
            "${CharacterDetailModuleType.UNIT_ICON.id}-${CharacterDetailModuleType.UNIQUE_EQUIP.id}-${CharacterDetailModuleType.SKILL.id}-"
        }
        navViewModel.characterDetailOrderData.postValue(orderData)
    }
    //选中的
    val mainList = orderData.intArrayList
    //未选中的
    val subList = arrayListOf(
        CharacterDetailModuleType.UNIT_ICON.id,
        CharacterDetailModuleType.CARD.id,
        CharacterDetailModuleType.COE.id,
        CharacterDetailModuleType.TOOLS.id,
        CharacterDetailModuleType.STAR.id,
        CharacterDetailModuleType.LEVEL.id,
        CharacterDetailModuleType.ATTR.id,
        CharacterDetailModuleType.OTHER_TOOLS.id,
        CharacterDetailModuleType.EQUIP.id,
        CharacterDetailModuleType.UNIQUE_EQUIP.id,
        CharacterDetailModuleType.SKILL.id,
    ).filter {
        !orderData.intArrayList.contains(it)
    }

    //水平分页相关
    val pageCount = if (showDetail && subList.isNotEmpty()) 2 else 1
    val pagerState = rememberPagerState { pageCount }
    val scrollState0 = rememberScrollState()
    val scrollState1 = rememberScrollState()

    //确认时监听
    val ok = navViewModel.fabOKClick.observeAsState().value ?: false
    if (ok && isEditMode) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOKClick.postValue(false)
        isEditMode = false
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        //页面
        if (visible) {
            if (isEditMode) {
                val typeList = arrayListOf(
                    CharacterDetailModuleType.CARD,
                    CharacterDetailModuleType.COE,
                    CharacterDetailModuleType.TOOLS,
                    CharacterDetailModuleType.STAR,
                    CharacterDetailModuleType.LEVEL,
                    CharacterDetailModuleType.ATTR,
                    CharacterDetailModuleType.OTHER_TOOLS,
                    CharacterDetailModuleType.EQUIP,
                    CharacterDetailModuleType.UNIQUE_EQUIP,
                    CharacterDetailModuleType.SKILL_LOOP,
                    CharacterDetailModuleType.SKILL,
                    CharacterDetailModuleType.UNIT_ICON,
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // 编辑模式
                    Subtitle2(
                        text = stringResource(R.string.order_character_detail),
                        modifier = Modifier
                            .padding(vertical = Dimen.mediumPadding)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    typeList.forEach {
                        Section(
                            id = it.id,
                            titleId = it.titleId,
                            isEditMode = true,
                            orderStr = orderData,
                            onClick = {
                                editOrder(it.id)
                            }
                        ) {}
                    }


                    CommonSpacer()
                    Spacer(modifier = Modifier.height(Dimen.fabSize + Dimen.fabMargin))
                }

            } else {
                HorizontalPager(state = pagerState) { index ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(if (index == 0) scrollState0 else scrollState1),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val list = if (index == 0) {
                            mainList
                        } else {
                            subList
                        }
                        list.forEach {
                            when (CharacterDetailModuleType.getByValue(it)) {

                                //角色卡面
                                CharacterDetailModuleType.CARD -> CharacterCard(
                                    unitId = unitId,
                                    basicInfo = basicInfo,
                                    loved = loved.value,
                                    actions
                                )

                                //战力
                                CharacterDetailModuleType.COE ->
                                    CharacterCoe(
                                        characterAttrData = characterAttrData,
                                        currentValue = currentValueState.value,
                                        toCoe = actions.toCoe
                                    )

                                //资料
                                CharacterDetailModuleType.TOOLS ->
                                    CharacterTools(
                                        unitId = unitId,
                                        actions = actions
                                    )

                                //星级
                                CharacterDetailModuleType.STAR -> StarSelect(
                                    currentValueState = currentValueState,
                                    max = maxValue.rarity
                                )

                                //等级
                                CharacterDetailModuleType.LEVEL -> CharacterLevel(
                                    currentValueState = currentValueState,
                                    maxValue.level
                                )

                                //属性
                                CharacterDetailModuleType.ATTR ->
                                    AttrLists(
                                        unitId,
                                        characterAttrData,
                                        actions.toCharacterStoryDetail
                                    )

                                //其他功能
                                CharacterDetailModuleType.OTHER_TOOLS -> CharacterOtherTools(
                                    unitId = unitId,
                                    actions = actions,
                                    currentValueState = currentValueState,
                                    maxRank = maxValue.rank
                                )

                                //装备
                                CharacterDetailModuleType.EQUIP -> CharacterEquip(
                                    unitId = unitId,
                                    currentValueState = currentValueState,
                                    maxRank = maxValue.rank,
                                    equips = characterAttrData.equips,
                                    toEquipDetail = actions.toEquipDetail,
                                    toCharacterRankEquip = actions.toCharacterRankEquip
                                )

                                //专武
                                CharacterDetailModuleType.UNIQUE_EQUIP -> characterAttrData.uniqueEquipList
                                    .forEachIndexed { index, uniqueEquipmentMaxData ->
                                        UniqueEquip(
                                            slot = index + 1,
                                            currentValueState = currentValueState,
                                            uniqueEquipLevelMax = if (index == 0) maxValue.uniqueEquipmentLevel else 5,
                                            uniqueEquipmentMaxData = uniqueEquipmentMaxData,
                                        )
                                    }

                                //技能列表
                                CharacterDetailModuleType.SKILL -> SkillLayout(
                                    normalSkillData = normalSkillData,
                                    spSkillData = spSkillData,
                                    spLabel = spLabel,
                                    unitType = UnitType.CHARACTER,
                                    toSummonDetail = actions.toSummonDetail,
                                    isFilterSkill = !showDetail,
                                    filterSkillCount = characterAttrData.uniqueEquipList.size,
                                    property = currentValueState.value
                                )

                                //图标
                                CharacterDetailModuleType.UNIT_ICON -> UnitIconAndTag(basicInfo)

                                //技能循环
                                CharacterDetailModuleType.SKILL_LOOP -> CharacterSkillLoop(
                                    unitId,
                                    scrollable = false
                                )

                                CharacterDetailModuleType.UNKNOWN -> {
                                    CenterTipText(stringResource(id = R.string.unknown))
                                }
                            }
                        }

                        CommonSpacer()
                        Spacer(modifier = Modifier.height(Dimen.fabSize + Dimen.fabMargin))
                    }
                }
            }
        }


        //未登场角色
        if (unknown) {
            CenterTipText(text = stringResource(R.string.unknown_character))
        }
        //悬浮按钮
        if (!unknown) {
            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalAlignment = Alignment.End
            ) {
                if (cutinId != 0 && showDetail) {
                    Row(
                        modifier = Modifier.padding(
                            end = Dimen.fabMargin
                        )
                    ) {
                        //角色技能形态
                        MainSmallFab(
                            iconType = if (isCutinSkill.value) {
                                MainIconType.CHARACTER_CUTIN_SKILL
                            } else {
                                MainIconType.CHARACTER_NORMAL_SKILL
                            },
                            text = if (isCutinSkill.value) {
                                stringResource(id = R.string.cutin_skill)
                            } else {
                                ""
                            },
                        ) {
                            isCutinSkill.value = !isCutinSkill.value
                        }
                    }
                }
                Box(
                    modifier = Modifier.padding(
                        bottom = Dimen.fabMargin
                    ),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        modifier = Modifier.padding(end = Dimen.fabMarginEnd)
                    ) {
                        if (showDetail) {
                            if (!isEditMode) {
                                //编辑
                                MainSmallFab(
                                    iconType = MainIconType.EDIT_TOOL,
                                ) {
                                    navViewModel.fabMainIcon.postValue(MainIconType.OK)
                                    isEditMode = true
                                }

                                //收藏
                                MainSmallFab(
                                    iconType = if (loved.value) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
                                ) {
                                    FilterCharacter.addOrRemove(unitId)
                                    loved.value = !loved.value
                                }
                            }

                            //技能循环
                            if (!orderData.contains(CharacterDetailModuleType.SKILL_LOOP.id.toString())) {
                                MainSmallFab(
                                    iconType = MainIconType.SKILL_LOOP,
                                ) {
                                    if (!isEditMode) {
                                        actions.toCharacterSkillLoop(currentIdState.intValue)
                                    }
                                }
                            }

                        } else {
                            //切换详情，专用装备跳转过来时，显示该按钮
                            MainSmallFab(
                                iconType = MainIconType.CHARACTER,
                                text = stringResource(id = R.string.character_detail)
                            ) {
                                showDetailState?.let {
                                    it.value = !showDetailState.value
                                }
                            }
                        }
                    }

                    //页面指示器
                    if (pageCount == 2 && !isEditMode) {
                        MainHorizontalPagerIndicator(
                            modifier = Modifier.padding(end = Dimen.smallPadding),
                            pagerState = pagerState,
                            pageCount = 2
                        )
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
    basicInfo: CharacterInfo?,
    loved: Boolean,
    actions: NavActions,
) {

    Column(
        modifier = Modifier
            .padding(
                top = Dimen.largePadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding,
                bottom = Dimen.smallPadding,
            )
            .widthIn(max = getItemWidth() * 1.3f)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //卡面信息
        Box {
            CharacterItem(
                unitId = unitId, character = basicInfo, loved = loved
            ) {
                actions.toAllPics(unitId, AllPicsType.CHARACTER.type)
            }
        }

    }

}

/**
 * 角色功能
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CharacterTools(
    unitId: Int,
    cutinId: Int = 0,
    actions: NavActions,
) {

    FlowRow(
        modifier = Modifier
            .padding(horizontal = Dimen.largePadding),
        horizontalArrangement = Arrangement.Center
    ) {

        //资料
        IconTextButton(
            icon = MainIconType.CHARACTER_INTRO,
            text = stringResource(id = R.string.character_basic_info)
        ) {
            actions.toCharacterBasicInfo(unitId)
        }
        //立绘预览
        IconTextButton(
            icon = MainIconType.PREVIEW_IMAGE,
            text = stringResource(id = R.string.character_pic),
            modifier = Modifier.padding(end = Dimen.smallPadding)
        ) {
            actions.toAllPics(unitId, AllPicsType.CHARACTER.type)
        }
        //模型预览
        IconTextButton(
            icon = MainIconType.PREVIEW_UNIT_SPINE,
            text = stringResource(id = R.string.spine_preview)
        ) {
            val id = if (cutinId != 0) cutinId else unitId
            BrowserUtil.open(Constants.PREVIEW_UNIT_URL + id)
        }
    }

}

/**
 * 其他功能
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CharacterOtherTools(
    unitId: Int,
    currentValueState: MutableState<CharacterProperty>,
    maxRank: Int,
    actions: NavActions,
) {

    //RANK相关功能
    FlowRow(
        modifier = Modifier
            .padding(horizontal = Dimen.largePadding),
        horizontalArrangement = Arrangement.Center
    ) {
        //RANK对比
        IconTextButton(
            icon = MainIconType.RANK_COMPARE,
            text = stringResource(id = R.string.rank_compare),
            iconSize = Dimen.fabIconSize,
            textStyle = MaterialTheme.typography.bodyMedium
        ) {
            actions.toCharacterRankCompare(
                unitId,
                maxRank,
                currentValueState.value.level,
                currentValueState.value.rarity,
                currentValueState.value.uniqueEquipmentLevel,
                currentValueState.value.uniqueEquipmentLevel2,
            )
        }
        //装备统计
        IconTextButton(
            icon = MainIconType.EQUIP_CALC,
            text = stringResource(id = R.string.calc_equip_count),
            iconSize = Dimen.fabIconSize,
            textStyle = MaterialTheme.typography.bodyMedium
        ) {
            actions.toCharacterEquipCount(unitId, maxRank)
        }
        //ex装备
        IconTextButton(
            icon = MainIconType.EXTRA_EQUIP,
            text = stringResource(id = R.string.tool_extra_equip),
            iconSize = Dimen.fabIconSize,
            textStyle = MaterialTheme.typography.bodyMedium
        ) {
            actions.toCharacterExtraEquip(unitId)
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
    toCoe: () -> Unit,
    attrViewModel: CharacterAttrViewModel = hiltViewModel()
) {
    //战力系数
    val coeValueFlow = remember {
        attrViewModel.getCoefficient()
    }
    val coeValue by coeValueFlow.collectAsState(initial = null)
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(start = Dimen.smallPadding)
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable {
                VibrateUtil(context).single()
                toCoe()
            }
            .padding(horizontal = Dimen.smallPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var value = ""

        coeValue?.let { coe ->
            val basicAttr = characterAttrData.sumAttr.copy().sub(characterAttrData.exSkillAttr)
            val basic =
                basicAttr.hp * coe.hp_coefficient + basicAttr.atk * coe.atk_coefficient + basicAttr.magicStr * coe.magic_str_coefficient + basicAttr.def * coe.def_coefficient + basicAttr.magicDef * coe.magic_def_coefficient + basicAttr.physicalCritical * coe.physical_critical_coefficient + basicAttr.magicCritical * coe.magic_critical_coefficient + basicAttr.waveHpRecovery * coe.wave_hp_recovery_coefficient + basicAttr.waveEnergyRecovery * coe.wave_energy_recovery_coefficient + basicAttr.dodge * coe.dodge_coefficient + basicAttr.physicalPenetrate * coe.physical_penetrate_coefficient + basicAttr.magicPenetrate * coe.magic_penetrate_coefficient + basicAttr.lifeSteal * coe.life_steal_coefficient + basicAttr.hpRecoveryRate * coe.hp_recovery_rate_coefficient + basicAttr.energyRecoveryRate * coe.energy_recovery_rate_coefficient + basicAttr.energyReduceRate * coe.energy_reduce_rate_coefficient + basicAttr.accuracy * coe.accuracy_coefficient
            //技能2：默认加上技能2
            var skill = currentValue.level * coe.skill_lv_coefficient
            //技能1：解锁专武，技能1系数提升
            if (characterAttrData.uniqueEquipList.isNotEmpty()) {
                skill += coe.skill1_evolution_coefficient * characterAttrData.uniqueEquipList.size
                skill += currentValue.level * coe.skill_lv_coefficient * coe.skill1_evolution_slv_coefficient * characterAttrData.uniqueEquipList.size
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
            value = (basic + skill).int.toString()
        }
        //战力数值
        MainText(
            text = stringResource(id = R.string.attr_all_value, value),
        )
        MainIcon(
            data = MainIconType.HELP, size = Dimen.smallIconSize
        )
    }
}

/**
 * 角色等级
 */
@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class
)
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
    Text(
        text = currentValueState.value.level.toString(),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth(0.3f)
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
        onValueChange = {
            var filterStr = ""
            it.deleteSpace.forEach { ch ->
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
        },
        shape = MaterialTheme.shapes.medium,
        textStyle = MaterialTheme.typography.bodyMedium,
        trailingIcon = {
            MainIcon(
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
                    currentValueState.value = currentValueState.value.update(
                        level = inputLevel.value.toInt()
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
        }.animateContentSize(defaultTween())
    )
}

/**
 * 属性
 */
@Composable
private fun AttrLists(
    unitId: Int,
    allData: AllAttrData,
    toCharacterStoryDetail: (Int) -> Unit
) {
    val context = LocalContext.current

    Spacer(modifier = Modifier.height(Dimen.largePadding))
    //属性
    AttrList(attrs = allData.sumAttr.all(isPreview = LocalInspectionMode.current))
    //剧情属性
    val storyAttrList = allData.storyAttr.allNotZero(isPreview = LocalInspectionMode.current)
    if (storyAttrList.isNotEmpty()) {
        Row(
            modifier = Modifier
                .padding(
                    top = Dimen.largePadding, bottom = Dimen.smallPadding
                )
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable {
                    VibrateUtil(context).single()
                    toCharacterStoryDetail(unitId)
                }
                .padding(horizontal = Dimen.smallPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            MainText(text = stringResource(id = R.string.title_story_attr))
            MainIcon(
                data = MainIconType.HELP, size = Dimen.smallIconSize
            )
        }
        AttrList(attrs = storyAttrList)
    }
    //Rank 奖励
    val rankBonusList =
        allData.rankBonus.attr.allNotZero(isPreview = LocalInspectionMode.current)
    if (rankBonusList.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainText(
                text = stringResource(id = R.string.title_rank_bonus),
                modifier = Modifier.Companion.padding(
                    top = Dimen.largePadding, bottom = Dimen.smallPadding
                ),
                textAlign = TextAlign.Center
            )
            AttrList(attrs = rankBonusList)
        }
    }
    Spacer(modifier = Modifier.height(Dimen.largePadding))
}

/**
 * 角色 RANK 装备
 */
@Composable
private fun CharacterEquip(
    unitId: Int,
    currentValueState: MutableState<CharacterProperty>,
    maxRank: Int,
    equips: List<EquipmentMaxData>,
    toEquipDetail: (Int) -> Unit,
    toCharacterRankEquip: (Int, Int) -> Unit
) {
    val rank = currentValueState.value.rank

    Column(
        modifier = Modifier
            .padding(vertical = Dimen.largePadding)
            .fillMaxWidth(),
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
            MainIcon(data = ImageRequestHelper.getInstance().getEquipPic(id6)) {
                if (id6 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id6)
                }
            }
            MainIcon(data = ImageRequestHelper.getInstance().getEquipPic(id3)) {
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
            MainIcon(data = ImageRequestHelper.getInstance().getEquipPic(id5)) {
                if (id5 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id5)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                MainIcon(data = MainIconType.BACK,
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
                    toCharacterRankEquip(unitId, rank)
                }
                MainIcon(data = MainIconType.MORE,
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
            MainIcon(data = ImageRequestHelper.getInstance().getEquipPic(id2)) {
                if (id2 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id2)
                }
            }

        }
        //装备 4、 1
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(Dimen.iconSize * 4)
        ) {
            val id4 = equips[4].equipmentId
            val id1 = equips[5].equipmentId
            MainIcon(data = ImageRequestHelper.getInstance().getEquipPic(id4)) {
                if (id4 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id4)
                }

            }
            MainIcon(data = ImageRequestHelper.getInstance().getEquipPic(id1)) {
                if (id1 != UNKNOWN_EQUIP_ID) {
                    toEquipDetail(id1)
                }
            }
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
    max: Int
) {

    Row(modifier = Modifier.padding(Dimen.mediumPadding)) {
        for (i in 1..max) {
            val iconId = when {
                i > currentValueState.value.rarity -> R.drawable.ic_star_dark
                i == 6 -> R.drawable.ic_star_pink
                else -> R.drawable.ic_star
            }
            MainIcon(
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

/**
 * 编辑排序
 */
private fun editOrder(id: Int) {
    val sp = mainSP()
    val orderStr = sp.getString(Constants.SP_CHARACTER_DETAIL_ORDER, "") ?: ""
    val idStr = "$id-"
    val hasAdded = orderStr.intArrayList.contains(id)

    //新增或移除
    val edited = if (!hasAdded) {
        orderStr + idStr
    } else {
        orderStr.replace(idStr, "")
    }
    sp.edit {
        putString(Constants.SP_CHARACTER_DETAIL_ORDER, edited)
        //更新
        navViewModel.characterDetailOrderData.postValue(edited)
    }
}

@CombinedPreviews
@Composable
private fun AttrListsPreview() {
    PreviewLayout {
        AttrLists(
            unitId = 100101,
            allData = AllAttrData(
                sumAttr = Attr().random(),
                storyAttr = Attr().random(),
                rankBonus = UnitPromotionBonus(attr = Attr().random()),
            ),
            toCharacterStoryDetail = { },
        )
    }
}

@CombinedPreviews
@Composable
private fun CharacterEquipPreview() {
    val currentValueState = remember {
        mutableStateOf(CharacterProperty())
    }
    PreviewLayout {
        CharacterEquip(
            unitId = 100101,
            currentValueState = currentValueState,
            maxRank = 20,
            equips = arrayListOf(
                EquipmentMaxData(),
                EquipmentMaxData(),
                EquipmentMaxData(),
                EquipmentMaxData(),
                EquipmentMaxData(),
                EquipmentMaxData()
            ),
            toEquipDetail = { },
            toCharacterRankEquip = { _, _ -> }
        )
    }
}