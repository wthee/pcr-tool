package cn.wthee.pcrtool.ui.equip

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentBasicInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.EquipGroupData
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.data.model.isFilter
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.components.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import kotlinx.coroutines.launch
import kotlin.math.min

//装备列表最大搜索数
private const val MAX_SEARCH_COUNT = 5

/**
 * 装备列表
 */
@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class
)
@Composable
fun EquipList(
    scrollState: LazyListState,
    viewModel: EquipmentViewModel = hiltViewModel(),
    toEquipDetail: (Int) -> Unit,
    toEquipMaterial: (Int) -> Unit,
    toSearchEquipQuest: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    //筛选状态
    val filter = navViewModel.filterEquip.observeAsState()
    //合成类型
    val craftIndex = remember {
        mutableStateOf(1)
    }
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    //搜索编号
    val searchEquipIdList = navViewModel.searchEquipIdList.observeAsState().value ?: arrayListOf()
    //搜索模式
    val searchEquipMode = navViewModel.searchEquipMode.observeAsState().value ?: false
    if (searchEquipMode) {
        craftIndex.value = 0
    }

    //关闭时监听
    if (!state.isVisible && !searchEquipMode) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOKClick.postValue(false)
        keyboardController?.hide()
    }


    val colorNum by viewModel.getEquipColorNum().collectAsState(initial = 0)

    filter.value?.let { filterValue ->
        filterValue.starIds = FilterEquipment.getStarIdList()
        val equips by viewModel.getEquips(filterValue).collectAsState(initial = arrayListOf())
        //分组
        val equipGroupList = arrayListOf<EquipGroupData>()
        equips.forEach { equip ->
            var group = equipGroupList.find {
                it.promotionLevel == equip.promotionLevel && it.requireLevel == equip.requireLevel
            }
            if (group == null) {
                group = EquipGroupData(equip.promotionLevel, equip.requireLevel)
                equipGroupList.add(group)
            }
            group.equipIdList.add(equip)
        }

        ModalBottomSheetLayout(
            sheetState = state,
            scrimColor = if (isSystemInDarkTheme()) colorAlphaBlack else colorAlphaWhite,
            sheetBackgroundColor = MaterialTheme.colorScheme.surface,
            sheetShape = shapeTop(),
            sheetContent = {
                FilterEquipSheet(colorNum, state, craftIndex)
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (equips.isNotEmpty()) {
                    LazyColumn(state = scrollState) {
                        items(items = equipGroupList,
                            key = {
                                "${it.requireLevel}-${it.promotionLevel}"
                            }) { equipGroupData ->
                            EquipGroup(
                                equipGroupData,
                                filterValue,
                                toEquipDetail,
                                toEquipMaterial,
                                searchEquipMode,
                                searchEquipIdList
                            )
                        }
                        items(2) {
                            CommonSpacer()
                        }
                    }
                } else {
                    CenterTipText(
                        stringResource(id = R.string.no_data)
                    )
                }

                //搜索装备掉落
                if (searchEquipMode) {
                    SearchEquip(
                        searchEquipIdList = searchEquipIdList,
                        toSearchEquipQuest = toSearchEquipQuest
                    )
                } else {
                    //切换至选择模式
                    MainSmallFab(
                        iconType = MainIconType.SEARCH,
                        text = stringResource(id = R.string.equip_serach_mode),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(
                                end = Dimen.fabMargin,
                                bottom = Dimen.fabMargin * 2 + Dimen.fabSize
                            ),
                    ) {
                        navViewModel.searchEquipMode.postValue(true)
                    }
                }


                Row(
                    modifier = Modifier
                        .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                        .align(Alignment.BottomEnd),
                    horizontalArrangement = Arrangement.End
                ) {
                    //回到顶部
                    MainSmallFab(
                        iconType = MainIconType.TOP
                    ) {
                        coroutineScope.launch {
                            scrollState.scrollToItem(0)
                        }
                    }
                    //重置筛选
                    if (filter.value != null && filter.value!!.isFilter()) {
                        MainSmallFab(
                            iconType = MainIconType.RESET
                        ) {
                            coroutineScope.launch {
                                state.hide()
                            }
                            navViewModel.resetClick.postValue(true)
                        }
                    }
                    val count = equips.size
                    // 数量显示&筛选按钮
                    MainSmallFab(
                        iconType = MainIconType.EQUIP,
                        text = "$count"
                    ) {
                        coroutineScope.launch {
                            navViewModel.fabMainIcon.postValue(MainIconType.OK)
                            state.show()
                        }
                    }
                }

            }

        }
    }

}

/**
 * 装备搜索组件
 */
@Composable
private fun SearchEquip(
    searchEquipIdList: List<Int>,
    toSearchEquipQuest: (String) -> Unit,
) {
    val context = LocalContext.current
    val openDialog = navViewModel.openChangeDataDialog.observeAsState().value ?: false
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    val mainIcon = navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.BACK
    //切换关闭监听
    if (close) {
        navViewModel.openChangeDataDialog.postValue(false)
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabCloseClick.postValue(false)
    }
    if (mainIcon == MainIconType.BACK) {
        navViewModel.openChangeDataDialog.postValue(false)
    }


    Box(modifier = Modifier.clickClose(openDialog)) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = Dimen.fabMargin,
                    bottom = Dimen.fabMargin * 2 + Dimen.fabSize
                ),
            verticalAlignment = Alignment.Bottom
        ) {
            if (searchEquipIdList.isNotEmpty()) {
                //预览已选择的
                SmallFloatingActionButton(
                    modifier = Modifier
                        .padding(
                            start = Dimen.fabMargin,
                            end = Dimen.textfabMargin
                        ),
                    shape = if (openDialog) MaterialTheme.shapes.medium else CircleShape,
                    onClick = {
                        VibrateUtil(context).single()
                        if (!openDialog) {
                            navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                            navViewModel.openChangeDataDialog.postValue(true)
                        }
                    },
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = if (openDialog) {
                            Dimen.popupMenuElevation
                        } else {
                            Dimen.fabElevation
                        }
                    ),
                ) {
                    if (openDialog) {
                        Column(
                            modifier = Modifier.width(
                                min(
                                    ScreenUtil.getWidth().px2dp,
                                    ScreenUtil.getHeight().px2dp
                                ).dp
                            )
                        ) {
                            MainText(
                                text = stringResource(id = R.string.picked_equip),
                                modifier = Modifier.padding(
                                    start = Dimen.mediumPadding,
                                    end = Dimen.mediumPadding,
                                    top = Dimen.mediumPadding,
                                )
                            )
                            VerticalGrid(
                                modifier = Modifier.padding(Dimen.mediumPadding),
                                itemWidth = Dimen.iconSize,
                                contentPadding = Dimen.largePadding
                            ) {
                                searchEquipIdList.forEach {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        MainIcon(
                                            data = ImageRequestHelper.getInstance().getEquipPic(it),
                                        ) {
                                            selectEquip(searchEquipIdList, it)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = Dimen.largePadding)
                        ) {
                            MainIcon(
                                data = MainIconType.BOX,
                                size = Dimen.fabIconSize
                            )
                            Text(
                                text = searchEquipIdList.size.toString(),
                                style = MaterialTheme.typography.titleSmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(
                                    start = Dimen.mediumPadding, end = Dimen.largePadding
                                )
                            )
                        }

                    }
                }
            }

            //搜索
            val tipSearch = stringResource(id = R.string.tip_equip_serach)
            MainSmallFab(
                iconType = MainIconType.SEARCH,
                text = stringResource(id = R.string.equip_serach)
            ) {
                if (searchEquipIdList.isNotEmpty()) {
                    toSearchEquipQuest(searchEquipIdList.listJoinStr)
                } else {
                    ToastUtil.short(tipSearch)
                }
            }
        }

    }
}

/**
 * 装备分组
 */
@Composable
private fun EquipGroup(
    equipGroupData: EquipGroupData,
    filterValue: FilterEquipment,
    toEquipDetail: (Int) -> Unit,
    toEquipMaterial: (Int) -> Unit,
    searchEquipMode: Boolean,
    searchEquipIdList: List<Int>
) {
    //分组标题
    CommonGroupTitle(
        titleStart = stringResource(
            id = R.string.equip_require_level,
            equipGroupData.requireLevel
        ),
        titleEnd = equipGroupData.equipIdList.size.toString(),
        modifier = Modifier.padding(Dimen.mediumPadding),
        backgroundColor = getEquipColor(equipGroupData.promotionLevel)
    )

    //分组内容
    VerticalGrid(
        itemWidth = if (searchEquipMode) Dimen.iconSize else Dimen.iconSize * 3,
        contentPadding = Dimen.largePadding,
        modifier = Modifier.padding(
            start = Dimen.commonItemPadding,
            end = Dimen.commonItemPadding
        ),
    ) {
        equipGroupData.equipIdList.forEach { equip ->
            EquipItem(
                filterValue,
                equip,
                toEquipDetail,
                toEquipMaterial,
                searchEquipMode,
                searchEquipIdList
            )
        }
    }
}

/**
 * 装备
 */
@Composable
private fun EquipItem(
    filter: FilterEquipment,
    equip: EquipmentBasicInfo,
    toEquipDetail: (Int) -> Unit,
    toEquipMaterial: (Int) -> Unit,
    searchEquipMode: Boolean,
    searchEquipIdList: List<Int>
) {
    val context = LocalContext.current

    var equipState by remember { mutableStateOf(equip) }
    if (equipState != equip) {
        equipState = equip
    }
    var filterState by remember { mutableStateOf(filter) }
    if (filterState != filter) {
        filterState = filter
    }


    val equipIcon: @Composable () -> Unit by remember {
        mutableStateOf(
            {
                MainIcon(
                    data = ImageRequestHelper.getInstance().getEquipPic(equipState.equipmentId)
                )
            }
        )
    }
    val equipName: @Composable () -> Unit by remember {
        mutableStateOf(
            {
                MainContentText(
                    text = equipState.equipmentName,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    selectable = true,
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    color = if (filterState.starIds.contains(equipState.equipmentId)) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        )
    }

    Row(
        modifier = Modifier
            .padding(
                start = Dimen.smallPadding,
                end = Dimen.smallPadding,
                bottom = Dimen.mediumPadding
            )
            .fillMaxWidth()
            .animateContentSize(defaultTween())
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable {
                VibrateUtil(context).single()
                if (searchEquipMode) {
                    selectEquip(searchEquipIdList, equip.equipmentId)
                } else {
                    //点击跳转
                    if (equip.craftFlg == 1) {
                        toEquipDetail(equipState.equipmentId)
                    } else {
                        toEquipMaterial(equipState.equipmentId)
                    }
                }
            }
            .padding(Dimen.smallPadding)
    ) {
        Box(contentAlignment = Alignment.Center) {
            equipIcon()
            if (searchEquipMode && searchEquipIdList.contains(equip.equipmentId)) {
                SelectText(
                    selected = true,
                    text = stringResource(id = R.string.selected_mark),
                    margin = 0.dp
                )
            }
        }
        if (!searchEquipMode) {
            equipName()
        }
    }
}

/**
 * 选中或取消装备
 */
private fun selectEquip(
    searchEquipIdList: List<Int>,
    equipId: Int
) {
    //点击选择
    val newList = arrayListOf<Int>()
    newList.addAll(searchEquipIdList)
    if (newList.contains(equipId)) {
        newList.remove(equipId)
    } else {
        if (searchEquipIdList.size >= MAX_SEARCH_COUNT) {
            ToastUtil.short(
                getString(
                    R.string.equip_max_select_count,
                    MAX_SEARCH_COUNT
                )
            )
            return
        } else {
            newList.add(equipId)
        }
    }
    navViewModel.searchEquipIdList.postValue(newList)
}

/**
 * 装备筛选
 */
@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class
)
@Composable
private fun FilterEquipSheet(
    colorNum: Int,
    sheetState: ModalBottomSheetState,
    craftIndex: MutableState<Int>,
) {
    val filter = navViewModel.filterEquip.value ?: FilterEquipment()

    val textState = remember { mutableStateOf(filter.name) }
    filter.name = textState.value

    filter.craft = craftIndex.value
    //收藏筛选
    val loveIndex = remember {
        mutableStateOf(if (filter.all) 0 else 1)
    }
    filter.all = loveIndex.value == 0
    //装备类型
    val typeIndex = remember {
        mutableStateOf(filter.colorType)
    }
    filter.colorType = typeIndex.value

    //确认操作
    val ok = navViewModel.fabOKClick.observeAsState().value ?: false
    val reset = navViewModel.resetClick.observeAsState().value ?: false

    //重置或确认
    LaunchedEffect(sheetState.isVisible, reset, ok) {
        if (reset) {
            textState.value = ""
            loveIndex.value = 0
            typeIndex.value = 0
            craftIndex.value = 1
            filter.craft = 1
            navViewModel.resetClick.postValue(false)
            navViewModel.filterEquip.postValue(FilterEquipment())
            navViewModel.searchEquipMode.postValue(false)
            navViewModel.searchEquipIdList.postValue(arrayListOf())
        }
        if (ok) {
            sheetState.hide()
            navViewModel.filterEquip.postValue(filter)
            navViewModel.fabOKClick.postValue(false)
            navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current


    //选择状态
    Column(
        modifier = Modifier
            .padding(start = Dimen.largePadding, end = Dimen.largePadding)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        //装备名搜索
        OutlinedTextField(
            value = textState.value,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { textState.value = it.deleteSpace },
            textStyle = MaterialTheme.typography.labelLarge,
            leadingIcon = {
                MainIcon(
                    data = MainIconType.EQUIP,
                    size = Dimen.fabIconSize
                )
            },
            trailingIcon = {
                MainIcon(
                    data = MainIconType.SEARCH,
                    size = Dimen.fabIconSize
                ) {
                    keyboardController?.hide()
                    navViewModel.fabOKClick.postValue(true)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    navViewModel.fabOKClick.postValue(true)
                }
            ),
            maxLines = 1,
            singleLine = true,
            label = {
                Text(
                    text = stringResource(id = R.string.equip_name),
                    style = MaterialTheme.typography.labelLarge
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        //装备类型
        MainText(
            text = stringResource(id = R.string.equip_craft),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val craftChipData = arrayListOf(
            ChipData(0, stringResource(id = R.string.uncraft)),
            ChipData(1, stringResource(id = R.string.craft)),
        )
        ChipGroup(
            craftChipData,
            craftIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //收藏
        MainText(
            text = stringResource(id = R.string.title_love),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val loveChipData = arrayListOf(
            ChipData(0, stringResource(id = R.string.all)),
            ChipData(1, stringResource(id = R.string.loved)),
        )
        ChipGroup(
            loveChipData,
            loveIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //品级
        MainText(
            text = stringResource(id = R.string.equip_level_color),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val colorChipData =
            arrayListOf(ChipData(0, stringResource(id = R.string.all)))
        for (i in 1..colorNum) {
            colorChipData.add(ChipData(i, getEquipColorText(i)))
        }
        ChipGroup(
            colorChipData,
            typeIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        CommonSpacer()
    }
}

/**
 * 装备品级颜色名
 */
@Composable
private fun getEquipColorText(colorType: Int): String {
    return when (colorType) {
        1 -> stringResource(id = R.string.color_blue)
        2 -> stringResource(id = R.string.color_copper)
        3 -> stringResource(id = R.string.color_silver)
        4 -> stringResource(id = R.string.color_gold)
        5 -> stringResource(id = R.string.color_purple)
        6 -> stringResource(id = R.string.color_red)
        7 -> stringResource(id = R.string.color_green)
        8 -> stringResource(id = R.string.color_orange)
        9 -> stringResource(id = R.string.color_cyan)
        else -> stringResource(id = R.string.unknown)
    }
}

/**
 * 装备品级颜色
 */
private fun getEquipColor(colorType: Int): Color {
    return when (colorType) {
        1 -> colorBlue
        2 -> colorCopper
        3 -> colorSilver
        4 -> colorGold
        5 -> colorPurple
        6 -> colorRed
        7 -> colorGreen
        8 -> colorOrange
        9 -> colorCyan
        else -> colorGray
    }
}


@CombinedPreviews
@Composable
private fun EquipGroupPreview() {
    val name = stringResource(id = R.string.debug_short_text)
    PreviewLayout {
        EquipGroup(
            EquipGroupData(
                promotionLevel = 8,
                equipIdList = arrayListOf(
                    EquipmentBasicInfo(equipmentName = name),
                    EquipmentBasicInfo(equipmentName = name),
                    EquipmentBasicInfo(equipmentName = name),
                )
            ),
            FilterEquipment(),
            { },
            { },
            false,
            arrayListOf()
        )
    }
}
