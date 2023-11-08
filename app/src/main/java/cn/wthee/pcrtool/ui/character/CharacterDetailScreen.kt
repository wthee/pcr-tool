package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.UnitStatusCoefficient
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.CharacterDetailModuleType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.navigation.getData
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.components.AttrList
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainHorizontalPagerIndicator
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.SubButton
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.components.getRankColor
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.skill.SkillListScreen
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.defaultTween
import cn.wthee.pcrtool.ui.tool.uniqueequip.UniqueEquipContent
import cn.wthee.pcrtool.ui.tool.uniqueequip.UnitIconAndTag
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.getFormatText
import cn.wthee.pcrtool.utils.int


/**
 * 角色信息
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun CharacterDetailScreen(
    actions: NavActions,
    characterDetailViewModel: CharacterDetailViewModel = hiltViewModel()
) {
    val uiState by characterDetailViewModel.uiState.collectAsStateWithLifecycle()

    //rank 装备选择监听
    LaunchedEffect(MainActivity.navSheetState.isVisible) {
        if (!MainActivity.navSheetState.isVisible) {
            val currentRank = getData<Int>(NavRoute.RANK)
            if (currentRank != null) {
                characterDetailViewModel.updateCurrentValue(uiState.currentValue.copy(rank = currentRank))
            }
        }
    }

    //页面状态
    val pagerState = rememberPagerState { uiState.pageCount }

    MainScaffold(
        fab = {
            CharacterDetailFabContent(
                loadingState = uiState.loadingState,
                currentId = uiState.currentId,
                showAllInfo = uiState.showAllInfo,
                isEditMode = uiState.isEditMode,
                loved = uiState.loved,
                orderData = uiState.orderData,
                changeEditMode = characterDetailViewModel::changeEditMode,
                updateStarCharacterId = characterDetailViewModel::updateStarId,
                toCharacterDetail = actions.toCharacterDetail,
                toCharacterSkillLoop = actions.toCharacterSkillLoop,
            )
        },
        secondLineFab = {
            ChangeCutinFabContent(
                loadingState = uiState.loadingState,
                cutinId = uiState.cutinId,
                showAllInfo = uiState.showAllInfo,
                isCutinSkill = uiState.isCutinSkill,
                changeCutin = characterDetailViewModel::changeCutin
            )
        },
        mainFabIcon = if(uiState.isEditMode) MainIconType.OK else MainIconType.EDIT_TOOL,
        onMainFabClick = {
            if(uiState.isEditMode){
                characterDetailViewModel.changeEditMode(false)
            }else{
                navigateUp()
            }
        }
    ) {
        StateBox(
            stateType = uiState.loadingState,
            errorContent = {
                //未登场角色
                CenterTipText(text = stringResource(R.string.unknown_character))
            }
        ) {
            CharacterDetailContent(
                uiState = uiState,
                pagerState = pagerState,
                updateOrderData = characterDetailViewModel::updateOrderData,
                updateCurrentValue = characterDetailViewModel::updateCurrentValue,
                actions = actions
            )
        }

        //页面指示器
        if (uiState.pageCount == 2 && !uiState.isEditMode && uiState.loadingState == LoadingState.Success) {
            MainHorizontalPagerIndicator(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = Dimen.exSmallPadding,
                        bottom = Dimen.fabSize / 2 + Dimen.fabMargin
                    ),
                pagerState = pagerState,
                pageCount = uiState.pageCount
            )
        }
    }

}

/**
 * 悬浮按钮
 * 收藏、编辑、技能循环、技能形态、角色详情
 */
@Composable
private fun CharacterDetailFabContent(
    loadingState: LoadingState,
    currentId: Int,
    showAllInfo: Boolean,
    isEditMode: Boolean,
    loved: Boolean,
    orderData: String,
    changeEditMode: (Boolean) -> Unit,
    updateStarCharacterId: () -> Unit,
    toCharacterSkillLoop: (Int) -> Unit,
    toCharacterDetail: (Int) -> Unit,
) {
    if (loadingState == LoadingState.Success) {
        if (showAllInfo) {
            if (!isEditMode) {
                //编辑
                MainSmallFab(
                    iconType = MainIconType.EDIT_TOOL,
                ) {
                    changeEditMode(true)
                }

                //收藏
                MainSmallFab(
                    iconType = if (loved) {
                        MainIconType.LOVE_FILL
                    } else {
                        MainIconType.LOVE_LINE
                    },
                ) {
                    updateStarCharacterId()
                }
            }

            //技能循环
            if (!orderData.contains(CharacterDetailModuleType.SKILL_LOOP.id.toString())) {
                MainSmallFab(
                    iconType = MainIconType.SKILL_LOOP,
                ) {
                    if (!isEditMode) {
                        toCharacterSkillLoop(currentId)
                    }
                }
            }

        } else {
            //切换详情，专用装备跳转过来时，显示该按钮
            MainSmallFab(
                iconType = MainIconType.CHARACTER,
                text = stringResource(id = R.string.character_detail)
            ) {
                toCharacterDetail(currentId)
            }
        }
    }
}

@Composable
private fun ChangeCutinFabContent(
    loadingState: LoadingState,
    cutinId: Int,
    showAllInfo: Boolean,
    isCutinSkill: Boolean,
    changeCutin: () -> Unit,
) {
    if (loadingState == LoadingState.Success && cutinId != 0 && showAllInfo) {
        //角色技能形态
        MainSmallFab(
            modifier = Modifier.padding(
                end = Dimen.fabMargin,
                bottom = Dimen.fabMarginLargeBottom
            ),
            iconType = if (isCutinSkill) {
                MainIconType.CHARACTER_CUTIN_SKILL
            } else {
                MainIconType.CHARACTER_NORMAL_SKILL
            },
            text = if (isCutinSkill) {
                stringResource(id = R.string.cutin_skill)
            } else {
                ""
            },
        ) {
            changeCutin()
        }
    }
}


@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun CharacterDetailContent(
    uiState: CharacterDetailUiState,
    pagerState: PagerState,
    actions: NavActions,
    updateOrderData: (Int) -> Unit,
    updateCurrentValue: (CharacterProperty) -> Unit,
) {

    val scrollState0 = rememberScrollState()
    val scrollState1 = rememberScrollState()


    if (uiState.isEditMode) {
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
                    orderStr = uiState.orderData,
                    onClick = {
                        updateOrderData(it.id)
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
                    uiState.mainList
                } else {
                    uiState.subList
                }
                list.forEach {
                    when (CharacterDetailModuleType.getByValue(it)) {

                        //角色卡面
                        CharacterDetailModuleType.CARD -> CharacterCard(
                            unitId = uiState.unitId,
                            basicInfo = uiState.basicInfo,
                            loved = uiState.loved,
                            toAllPics = actions.toAllPics
                        )

                        //战力
                        CharacterDetailModuleType.COE ->
                            CharacterCoeContent(
                                coeValue = uiState.coeValue,
                                allAttr = uiState.allAttr,
                                currentValue = uiState.currentValue,
                                toCoe = actions.toCoe
                            )

                        //资料
                        CharacterDetailModuleType.TOOLS ->
                            ToolsContent(
                                unitId = uiState.unitId,
                                cutinId = uiState.cutinId,
                                toCharacterBasicInfo = actions.toCharacterBasicInfo,
                                toAllPics = actions.toAllPics
                            )

                        //星级
                        CharacterDetailModuleType.STAR -> StarSelectContent(
                            currentValue = uiState.currentValue,
                            max = uiState.maxValue.rarity,
                            updateCurrentValue = updateCurrentValue
                        )

                        //等级
                        CharacterDetailModuleType.LEVEL -> LevelContent(
                            currentValue = uiState.currentValue,
                            uiState.maxValue.level,
                            updateCurrentValue = updateCurrentValue
                        )

                        //属性
                        CharacterDetailModuleType.ATTR ->
                            AttrListContent(
                                unitId = uiState.unitId,
                                allAttr = uiState.allAttr,
                                toCharacterStoryDetail = actions.toCharacterStoryDetail
                            )

                        //其他功能
                        CharacterDetailModuleType.OTHER_TOOLS -> OtherToolsContent(
                            unitId = uiState.unitId,
                            currentValue = uiState.currentValue,
                            maxRank = uiState.maxValue.rank,
                            toCharacterRankCompare = actions.toCharacterRankCompare,
                            toCharacterEquipCount = actions.toCharacterEquipCount,
                            toCharacterExtraEquip = actions.toCharacterExtraEquip
                        )

                        //装备
                        CharacterDetailModuleType.EQUIP -> EquipContent(
                            rank = uiState.currentValue.rank,
                            unitId = uiState.unitId,
                            currentValue = uiState.currentValue,
                            maxRank = uiState.maxValue.rank,
                            equips = uiState.allAttr.equips,
                            updateCurrentValue = updateCurrentValue,
                            toEquipDetail = actions.toEquipDetail,
                            toCharacterRankEquip = actions.toCharacterRankEquip
                        )

                        //专武
                        CharacterDetailModuleType.UNIQUE_EQUIP -> uiState.allAttr.uniqueEquipList
                            .forEachIndexed { index, uniqueEquipmentMaxData ->
                                UniqueEquipContent(
                                    slot = index + 1,
                                    currentValue = uiState.currentValue,
                                    uniqueEquipLevelMax = if (index == 0) {
                                        uiState.maxValue.uniqueEquipmentLevel
                                    } else {
                                        5
                                    },
                                    uniqueEquipmentMaxData = uniqueEquipmentMaxData,
                                    updateCurrentValue = updateCurrentValue,
                                )
                            }

                        //技能列表
                        CharacterDetailModuleType.SKILL -> SkillListScreen(
                            unitId = uiState.currentId,
                            atk = uiState.maxAtk,
                            unitType = UnitType.CHARACTER_SUMMON,
                            property = uiState.currentValue,
                            toSummonDetail = actions.toSummonDetail,
                            isFilterSkill = !uiState.showAllInfo,
                            filterSkillCount = uiState.allAttr.uniqueEquipList.size,
                        )

                        //图标
                        CharacterDetailModuleType.UNIT_ICON -> UnitIconAndTag(uiState.basicInfo)

                        //技能循环
                        CharacterDetailModuleType.SKILL_LOOP -> CharacterSkillLoopScreen(
                            unitId = uiState.currentId,
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


/**
 * 角色卡面
 */
@Composable
private fun CharacterCard(
    unitId: Int,
    basicInfo: CharacterInfo?,
    loved: Boolean,
    toAllPics: (Int, Int) -> Unit
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
            CharacterItemContent(
                unitId = unitId, character = basicInfo, loved = loved
            ) {
                toAllPics(unitId, AllPicsType.CHARACTER.type)
            }
        }

    }

}

/**
 * 角色功能
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ToolsContent(
    unitId: Int,
    cutinId: Int,
    toCharacterBasicInfo: (Int) -> Unit,
    toAllPics: (Int, Int) -> Unit,
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
            toCharacterBasicInfo(unitId)
        }
        //立绘预览
        IconTextButton(
            icon = MainIconType.PREVIEW_IMAGE,
            text = stringResource(id = R.string.character_pic),
            modifier = Modifier.padding(end = Dimen.smallPadding)
        ) {
            toAllPics(unitId, AllPicsType.CHARACTER.type)
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
private fun OtherToolsContent(
    unitId: Int,
    currentValue: CharacterProperty,
    maxRank: Int,
    toCharacterRankCompare: (Int, Int, Int, Int, Int, Int) -> Unit,
    toCharacterEquipCount: (Int) -> Unit,
    toCharacterExtraEquip: (Int) -> Unit
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
            toCharacterRankCompare(
                unitId,
                maxRank,
                currentValue.level,
                currentValue.rarity,
                currentValue.uniqueEquipmentLevel,
                currentValue.uniqueEquipmentLevel2,
            )
        }
        //装备统计
        IconTextButton(
            icon = MainIconType.EQUIP_CALC,
            text = stringResource(id = R.string.calc_equip_count),
            iconSize = Dimen.fabIconSize,
            textStyle = MaterialTheme.typography.bodyMedium
        ) {
            toCharacterEquipCount(unitId)
        }
        //ex装备
        IconTextButton(
            icon = MainIconType.EXTRA_EQUIP,
            text = stringResource(id = R.string.tool_extra_equip),
            iconSize = Dimen.fabIconSize,
            textStyle = MaterialTheme.typography.bodyMedium
        ) {
            toCharacterExtraEquip(unitId)
        }


    }
}

/**
 * 角色等级
 */
@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class
)
@Composable
private fun ColumnScope.LevelContent(
    currentValue: CharacterProperty,
    maxLevel: Int,
    updateCurrentValue: (CharacterProperty) -> Unit
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
        text = currentValue.level.toString(),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
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
                    updateCurrentValue(currentValue.copy(level = inputLevel.value.toInt()))
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
                    updateCurrentValue(currentValue.copy(level = inputLevel.value.toInt()))
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
private fun ColumnScope.AttrListContent(
    unitId: Int,
    allAttr: AllAttrData,
    toCharacterStoryDetail: (Int) -> Unit
) {
    val context = LocalContext.current

    Spacer(modifier = Modifier.height(Dimen.largePadding))
    //属性
    AttrList(attrs = allAttr.sumAttr.all(isPreview = LocalInspectionMode.current))
    //剧情属性
    val storyAttrList =
        allAttr.storyAttr.allNotZero(isPreview = LocalInspectionMode.current)
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
                .padding(horizontal = Dimen.smallPadding)
                .align(Alignment.CenterHorizontally),
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
        allAttr.rankBonus.attr.allNotZero(isPreview = LocalInspectionMode.current)
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
 * 战力计算
 */
@Composable
private fun CharacterCoeContent(
    coeValue: UnitStatusCoefficient?,
    allAttr: AllAttrData,
    currentValue: CharacterProperty,
    toCoe: () -> Unit
) {
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
            val basicAttr = allAttr.sumAttr.copy().sub(allAttr.exSkillAttr)
            val basic =
                basicAttr.hp * coe.hp_coefficient + basicAttr.atk * coe.atk_coefficient + basicAttr.magicStr * coe.magic_str_coefficient + basicAttr.def * coe.def_coefficient + basicAttr.magicDef * coe.magic_def_coefficient + basicAttr.physicalCritical * coe.physical_critical_coefficient + basicAttr.magicCritical * coe.magic_critical_coefficient + basicAttr.waveHpRecovery * coe.wave_hp_recovery_coefficient + basicAttr.waveEnergyRecovery * coe.wave_energy_recovery_coefficient + basicAttr.dodge * coe.dodge_coefficient + basicAttr.physicalPenetrate * coe.physical_penetrate_coefficient + basicAttr.magicPenetrate * coe.magic_penetrate_coefficient + basicAttr.lifeSteal * coe.life_steal_coefficient + basicAttr.hpRecoveryRate * coe.hp_recovery_rate_coefficient + basicAttr.energyRecoveryRate * coe.energy_recovery_rate_coefficient + basicAttr.energyReduceRate * coe.energy_reduce_rate_coefficient + basicAttr.accuracy * coe.accuracy_coefficient
            //技能2：默认加上技能2
            var skill = currentValue.level * coe.skill_lv_coefficient
            //技能1：解锁专武，技能1系数提升
            if (allAttr.uniqueEquipList.isNotEmpty()) {
                skill += coe.skill1_evolution_coefficient * allAttr.uniqueEquipList.size
                skill += currentValue.level * coe.skill_lv_coefficient * coe.skill1_evolution_slv_coefficient * allAttr.uniqueEquipList.size
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
 * 角色 RANK 装备
 */
@Composable
private fun EquipContent(
    unitId: Int,
    rank: Int,
    maxRank: Int,
    currentValue: CharacterProperty,
    equips: List<EquipmentMaxData>,
    updateCurrentValue: (CharacterProperty) -> Unit,
    toEquipDetail: (Int) -> Unit,
    toCharacterRankEquip: (Int, Int) -> Unit
) {

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
                if (currentValue.rank < maxRank) {
                    MainIcon(
                        data = MainIconType.BACK,
                        tint = getRankColor(rank = rank + 1),
                        onClick = {
                            updateCurrentValue(currentValue.copy(rank = rank + 1))
                        },
                        size = Dimen.mediumIconSize,
                        modifier = Modifier.padding(start = Dimen.mediumPadding)
                    )
                } else {
                    Spacer(modifier = Modifier.width(Dimen.mediumPadding + Dimen.mediumIconSize))
                }
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
                if (rank > 1) {
                    MainIcon(
                        data = MainIconType.MORE,
                        tint = getRankColor(rank = rank - 1),
                        onClick = {
                            updateCurrentValue(currentValue.copy(rank = rank - 1))
                        },
                        size = Dimen.mediumIconSize,
                        modifier = Modifier.padding(end = Dimen.mediumPadding)
                    )
                } else {
                    Spacer(modifier = Modifier.width(Dimen.mediumPadding + Dimen.mediumIconSize))
                }
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
private fun StarSelectContent(
    currentValue: CharacterProperty,
    max: Int,
    updateCurrentValue: (CharacterProperty) -> Unit
) {

    Row(modifier = Modifier.padding(Dimen.mediumPadding)) {
        for (i in 1..max) {
            val iconId = when {
                i > currentValue.rarity -> R.drawable.ic_star_dark
                i == 6 -> R.drawable.ic_star_pink
                else -> R.drawable.ic_star
            }
            MainIcon(
                data = iconId,
                size = Dimen.fabIconSize,
                modifier = Modifier.padding(Dimen.smallPadding)
            ) {
                updateCurrentValue(currentValue.copy(rarity = i))
            }

        }
    }
}

@CombinedPreviews
@Composable
private fun FabContentPreview() {
    PreviewLayout {
        CharacterDetailFabContent(
            loadingState = LoadingState.Success,
            currentId = 101001,
            showAllInfo = true,
            isEditMode = false,
            loved = true,
            orderData = "",
            changeEditMode = {},
            updateStarCharacterId = {},
            toCharacterSkillLoop = {},
            toCharacterDetail = {},
        )
    }
}

@CombinedPreviews
@Composable
private fun CharacterCardPreview() {
    PreviewLayout {
        CharacterCard(
            unitId = 100101,
            loved = true,
            basicInfo = CharacterInfo(
                id = 100101,
                position = 100
            ),
            toAllPics = { _, _ -> }
        )
    }
}

@CombinedPreviews
@Composable
private fun ToolsContentPreview() {
    PreviewLayout {
        ToolsContent(
            unitId = 100101,
            cutinId = 0,
            {},
            { _, _ -> }
        )
    }
}

@CombinedPreviews
@Composable
private fun OtherToolsContentPreview() {
    PreviewLayout {
        OtherToolsContent(
            unitId = 100101,
            currentValue = CharacterProperty(),
            maxRank = 1,
            toCharacterRankCompare = { _, _, _, _, _, _ -> },
            toCharacterEquipCount = {},
            toCharacterExtraEquip = {}
        )
    }
}

@CombinedPreviews
@Composable
private fun LevelContentPreview() {
    PreviewLayout {
        LevelContent(
            currentValue = CharacterProperty(),
            maxLevel = 100,
            updateCurrentValue = {}
        )
    }
}

@CombinedPreviews
@Composable
private fun AttrListContentPreview() {
    PreviewLayout {
        AttrListContent(
            unitId = 101001,
            allAttr = AllAttrData(),
            toCharacterStoryDetail = { },
        )
    }
}

@CombinedPreviews
@Composable
private fun CharacterCoeContentPreview() {
    PreviewLayout {
        CharacterCoeContent(
            coeValue = UnitStatusCoefficient(),
            allAttr = AllAttrData(),
            currentValue = CharacterProperty(),
            toCoe = { },
        )
    }
}

@CombinedPreviews
@Composable
private fun EquipContentPreview() {
    PreviewLayout {
        EquipContent(
            rank = 1,
            unitId = 101001,
            currentValue = CharacterProperty(),
            maxRank = 22,
            equips = arrayListOf(
                EquipmentMaxData(),
                EquipmentMaxData(),
                EquipmentMaxData(),
                EquipmentMaxData(),
                EquipmentMaxData(),
                EquipmentMaxData()
            ),
            updateCurrentValue = {},
            toEquipDetail = { },
            toCharacterRankEquip = { _, _ -> }
        )
    }
}

@CombinedPreviews
@Composable
private fun StarSelectContentPreview() {
    PreviewLayout {
        StarSelectContent(
            currentValue = CharacterProperty(rarity = 5),
            max = 6,
            updateCurrentValue = { }
        )
    }
}