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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.UnitPromotionBonus
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.CharacterDetailModuleType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.data.model.getStarCharacterIdList
import cn.wthee.pcrtool.data.model.updateStarCharacterId
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
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
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.skill.SkillCompose
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
import cn.wthee.pcrtool.utils.editOrder
import cn.wthee.pcrtool.utils.getFormatText
import cn.wthee.pcrtool.utils.intArrayList
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


private const val DEFAULT_ORDER = "300-301-302-303-304-305-306-307-308-310-"

/**
 * 角色信息
 *
 * @param unitId 角色编号
 * @param showAllInfo true：显示全部信息，false：仅显示专用装备相关信息,
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterDetailScreen(
    unitId: Int,
    actions: NavActions,
    showAllInfo: Boolean = true,
    characterDetailViewModel: CharacterDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by characterDetailViewModel.uiState.collectAsStateWithLifecycle()

    //最大值
    val maxValue = uiState.maxValue
    //当前选择的数值信息
    val currentValue =  uiState.currentValue
    //rank 装备选择监听
    val rankEquipSelected = navViewModel.rankEquipSelected.observeAsState().value ?: 0
    LaunchedEffect(rankEquipSelected) {
        if (rankEquipSelected != 0 && currentValue.rank != rankEquipSelected) {
            characterDetailViewModel.updateCurrentValue(currentValue.copy(rank = rankEquipSelected))
        }
    }
    //角色属性
    val characterAttrData = uiState.allAttr

    //基本信息
    val basicInfo = uiState.basicInfo

    //数据加载后，展示页面
    val visible = characterAttrData.sumAttr.hp > 1 && characterAttrData.equips.isNotEmpty()
    //未实装角色
    val unknown = maxValue.level == -1

    //收藏状态
    val loved = getStarCharacterIdList().contains(unitId)

    //编辑模式
    var isEditMode by remember {
        mutableStateOf(false)
    }

    //自定义显示顺序
    val orderData = if (showAllInfo) {
        remember {
            context.dataStoreMain.data.map {
                it[MainPreferencesKeys.SP_CHARACTER_DETAIL_ORDER] ?: DEFAULT_ORDER
            }
        }.collectAsState(initial = DEFAULT_ORDER).value
    } else {
        "${CharacterDetailModuleType.UNIT_ICON.id}-${CharacterDetailModuleType.UNIQUE_EQUIP.id}-${CharacterDetailModuleType.SKILL.id}-"
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
    val pageCount = if (showAllInfo && subList.isNotEmpty()) 2 else 1
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
                                scope.launch {
                                    editOrder(
                                        context,
                                        scope,
                                        it.id,
                                        MainPreferencesKeys.SP_CHARACTER_DETAIL_ORDER
                                    )
                                }
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
                                    loved = loved,
                                    actions
                                )

                                //战力
                                CharacterDetailModuleType.COE ->
                                    CharacterCoeScreen(
                                        characterAttrData = characterAttrData,
                                        currentValue = currentValue,
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
                                    currentValue = currentValue,
                                    max = maxValue.rarity,
                                    updateCurrentValue = characterDetailViewModel::updateCurrentValue
                                )

                                //等级
                                CharacterDetailModuleType.LEVEL -> CharacterLevel(
                                    currentValue = currentValue,
                                    maxValue.level,
                                    updateCurrentValue = characterDetailViewModel::updateCurrentValue
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
                                    currentValue = currentValue,
                                    maxRank = maxValue.rank
                                )

                                //装备
                                CharacterDetailModuleType.EQUIP -> CharacterEquip(
                                    unitId = unitId,
                                    currentValue = currentValue,
                                    maxRank = maxValue.rank,
                                    equips = characterAttrData.equips,
                                    updateCurrentValue = characterDetailViewModel::updateCurrentValue,
                                    toEquipDetail = actions.toEquipDetail,
                                    toCharacterRankEquip = actions.toCharacterRankEquip
                                )

                                //专武
                                CharacterDetailModuleType.UNIQUE_EQUIP -> characterAttrData.uniqueEquipList
                                    .forEachIndexed { index, uniqueEquipmentMaxData ->
                                        UniqueEquip(
                                            slot = index + 1,
                                            currentValue = currentValue,
                                            uniqueEquipLevelMax = if (index == 0) maxValue.uniqueEquipmentLevel else 5,
                                            uniqueEquipmentMaxData = uniqueEquipmentMaxData,
                                        )
                                    }

                                //技能列表
                                CharacterDetailModuleType.SKILL -> SkillCompose(
                                    unitId = uiState.currentId,
                                    atk = uiState.maxAtk,
                                    unitType = UnitType.CHARACTER_SUMMON,
                                    property = uiState.currentValue
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
                if (uiState.cutinId != 0 && showAllInfo) {
                    Row(
                        modifier = Modifier.padding(
                            end = Dimen.fabMargin
                        )
                    ) {
                        //角色技能形态
                        MainSmallFab(
                            iconType = if (uiState.isCutinSkill) {
                                MainIconType.CHARACTER_CUTIN_SKILL
                            } else {
                                MainIconType.CHARACTER_NORMAL_SKILL
                            },
                            text = if (uiState.isCutinSkill) {
                                stringResource(id = R.string.cutin_skill)
                            } else {
                                ""
                            },
                        ) {
                            characterDetailViewModel.changeCutin()
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
                        if (showAllInfo) {
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
                                    iconType = if (loved) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
                                ) {
                                    scope.launch {
                                        updateStarCharacterId(context, unitId)
                                    }
                                }
                            }

                            //技能循环
                            if (!orderData.contains(CharacterDetailModuleType.SKILL_LOOP.id.toString())) {
                                MainSmallFab(
                                    iconType = MainIconType.SKILL_LOOP,
                                ) {
                                    if (!isEditMode) {
                                        actions.toCharacterSkillLoop(uiState.cutinId)
                                    }
                                }
                            }

                        } else {
                            //切换详情，专用装备跳转过来时，显示该按钮
                            MainSmallFab(
                                iconType = MainIconType.CHARACTER,
                                text = stringResource(id = R.string.character_detail)
                            ) {
                                actions.toCharacterDetail(unitId)
                            }
                        }
                    }

                    //页面指示器
                    if (pageCount == 2 && !isEditMode) {
                        MainHorizontalPagerIndicator(
                            modifier = Modifier.padding(end = Dimen.exSmallPadding),
                            pagerState = pagerState,
                            pageCount = pageCount
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
    currentValue: CharacterProperty,
    maxRank: Int,
    actions: NavActions
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
 * 角色等级
 */
@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class
)
@Composable
private fun CharacterLevel(
    currentValue: CharacterProperty,
    maxLevel: Int,
    updateCurrentValue:(CharacterProperty)->Unit
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
    currentValue: CharacterProperty,
    maxRank: Int,
    equips: List<EquipmentMaxData>,
    updateCurrentValue: (CharacterProperty) -> Unit,
    toEquipDetail: (Int) -> Unit,
    toCharacterRankEquip: (Int, Int) -> Unit
) {
    val rank = currentValue.rank

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
                    tint = if (currentValue.rank < maxRank) {
                        getRankColor(rank = rank + 1)
                    } else {
                        Color.Transparent
                    },
                    onClick = if (rank < maxRank) {
                        {
                            updateCurrentValue(currentValue.copy(rank = rank + 1))
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
                            updateCurrentValue(currentValue.copy(rank = rank - 1))
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
                updateCurrentValue(currentValue.copy(rarity =i))
            }

        }
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
    PreviewLayout {
        CharacterEquip(
            unitId = 100101,
            currentValue = CharacterProperty(),
            maxRank = 20,
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