package cn.wthee.pcrtool.ui.equip

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentBasicInfo
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.EquipGroupData
import cn.wthee.pcrtool.data.model.FilterEquip
import cn.wthee.pcrtool.data.model.isFilter
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.components.CommonGroupTitle
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.ExpandableFab
import cn.wthee.pcrtool.ui.components.IconListContent
import cn.wthee.pcrtool.ui.components.LifecycleEffect
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.SelectText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.VerticalStaggeredGrid
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorBlue
import cn.wthee.pcrtool.ui.theme.colorCopper
import cn.wthee.pcrtool.ui.theme.colorCyan
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorOrange
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorSilver
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.listJoinStr
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


/**
 * 装备列表
 */
@Composable
fun EquipListScreen(
    toEquipDetail: (Int) -> Unit,
    toEquipMaterial: (Int, String) -> Unit,
    toSearchEquipQuest: (String) -> Unit,
    toFilterEquip: (String) -> Unit,
    equipListViewModel: EquipListViewModel = hiltViewModel(),
) {
    val uiState by equipListViewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()

    //初始筛选信息
    LifecycleEffect(Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_CREATE) {
        equipListViewModel.initFilter()
    }


    MainScaffold(
        fab = {
            EquipListFabContent(
                count = uiState.equipList?.size ?: 0,
                scrollState = scrollState,
                filter = uiState.filter,
                resetFilter = equipListViewModel::resetFilter,
                toFilterEquip = toFilterEquip
            )
        },
        secondLineFab = {
            //搜索装备掉落
            EquipSearchFabContent(
                openSearchDialog = uiState.openSearchDialog,
                searchEquipMode = uiState.searchEquipMode,
                searchEquipIdList = uiState.searchEquipIdList,
                toSearchEquipQuest = toSearchEquipQuest,
                changeSearchMode = equipListViewModel::changeSearchMode,
                selectEquip = equipListViewModel::selectEquip,
                changeSearchDialog = equipListViewModel::changeSearchDialog
            )
        },
        enableClickClose = uiState.openSearchDialog,
        onCloseClick = {
            equipListViewModel.changeSearchDialog(false)
        },
        mainFabIcon = if (uiState.openSearchDialog) MainIconType.CLOSE else MainIconType.BACK,
        onMainFabClick = {
            if (uiState.openSearchDialog) {
                equipListViewModel.changeSearchDialog(false)
            } else {
                navigateUp()
            }
        }
    ) {
        StateBox(
            stateType = uiState.loadState,
        ) {
            if (uiState.equipList != null && uiState.filter != null) {
                EquipListContent(
                    equipList = uiState.equipList!!,
                    scrollState = scrollState,
                    favoriteIdList = uiState.favoriteIdList,
                    toEquipDetail = toEquipDetail,
                    toEquipMaterial = toEquipMaterial,
                    searchEquipMode = uiState.searchEquipMode,
                    searchEquipIdList = uiState.searchEquipIdList,
                    selectEquip = equipListViewModel::selectEquip
                )
            }
        }
    }


}

/**
 * 装备搜索fab
 */
@Composable
private fun EquipSearchFabContent(
    openSearchDialog: Boolean,
    searchEquipMode: Boolean,
    searchEquipIdList: List<Int>,
    toSearchEquipQuest: (String) -> Unit,
    changeSearchMode: () -> Unit,
    selectEquip: (Int) -> Unit,
    changeSearchDialog: (Boolean) -> Unit
) {

    Box(
        modifier = Modifier
            .padding(
                end = Dimen.fabMargin,
                bottom = Dimen.fabMarginLargeBottom
            )
    ) {
        if (searchEquipMode) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                if (searchEquipIdList.isNotEmpty()) {
                    //预览已选择的
                    ExpandableFab(
                        paddingValues = PaddingValues(
                            start = Dimen.fabMargin,
                            end = Dimen.textFabMargin
                        ),
                        expanded = openSearchDialog,
                        onClick = {
                            changeSearchDialog(true)
                        },
                        icon = MainIconType.BOX,
                        text = searchEquipIdList.size.toString(),
                        animateContent = false
                    ) {
                        //展开已选择的装备
                        IconListContent(
                            idList = searchEquipIdList,
                            title = stringResource(id = R.string.picked_equip),
                            iconResourceType = IconResourceType.EQUIP,
                            onClickItem = selectEquip
                        )
                    }
                }

                //搜索
                if (!openSearchDialog) {
                    val tipSearch = stringResource(id = R.string.tip_equip_search)
                    MainSmallFab(
                        iconType = MainIconType.SEARCH,
                        text = stringResource(id = R.string.equip_search),
                        onClick = {
                            if (searchEquipIdList.isNotEmpty()) {
                                toSearchEquipQuest(searchEquipIdList.listJoinStr)
                            } else {
                                ToastUtil.short(tipSearch)
                            }
                        }
                    )
                }

            }
        } else {
            //切换至选择模式
            MainSmallFab(
                iconType = MainIconType.SEARCH,
                text = stringResource(id = R.string.equip_search_mode),
                onClick = {
                    changeSearchMode()
                }
            )
        }
    }
}

@Composable
private fun EquipListFabContent(
    count: Int,
    scrollState: LazyListState,
    filter: FilterEquip?,
    resetFilter: () -> Unit,
    toFilterEquip: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    //回到顶部
    MainSmallFab(
        iconType = MainIconType.TOP,
        onClick = {
            coroutineScope.launch {
                scrollState.scrollToItem(0)
            }
        }
    )

    //重置筛选
    if (filter?.isFilter() == true) {
        MainSmallFab(
            iconType = MainIconType.RESET,
            onClick = {
                resetFilter()
            }
        )
    }

    // 数量显示&筛选按钮
    MainSmallFab(
        iconType = MainIconType.EQUIP,
        text = "$count",
        onClick = {
            filter?.let {
                toFilterEquip(Json.encodeToString(filter))
            }
        }
    )
}

@Composable
private fun EquipListContent(
    equipList: List<EquipmentBasicInfo>,
    scrollState: LazyListState,
    favoriteIdList: List<Int>,
    searchEquipMode: Boolean,
    searchEquipIdList: List<Int>,
    toEquipDetail: (Int) -> Unit,
    toEquipMaterial: (Int, String) -> Unit,
    selectEquip: (Int) -> Unit,
) {

    //分组
    val equipGroupList = arrayListOf<EquipGroupData>()
    equipList.forEach { equip ->
        var group = equipGroupList.find {
            it.promotionLevel == equip.promotionLevel && it.requireLevel == equip.requireLevel
        }
        if (group == null) {
            group = EquipGroupData(equip.promotionLevel, equip.requireLevel)
            equipGroupList.add(group)
        }
        group.equipIdList.add(equip)
    }

    LazyColumn(state = scrollState) {
        items(items = equipGroupList,
            key = {
                "${it.requireLevel}-${it.promotionLevel}"
            }
        ) { equipGroupData ->
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
            if (!searchEquipMode) {
                VerticalStaggeredGrid(
                    itemWidth = Dimen.iconSize * 3,
                    contentPadding = Dimen.mediumPadding,
                    modifier = Modifier.padding(
                        horizontal = Dimen.commonItemPadding
                    )
                ) {
                    equipGroupData.equipIdList.forEach { equip ->
                        EquipItem(
                            favoriteIdList = favoriteIdList,
                            equip = equip,
                            toEquipDetail = toEquipDetail,
                            toEquipMaterial = toEquipMaterial,
                            searchEquipMode = false,
                            searchEquipIdList = searchEquipIdList,
                            selectEquip = selectEquip
                        )
                    }
                }
            } else {
                VerticalStaggeredGrid(
                    itemWidth = Dimen.iconItemWidth,
                    verticalContentPadding = Dimen.commonItemPadding,
                    modifier = Modifier.padding(
                        horizontal = Dimen.commonItemPadding
                    )
                ) {
                    equipGroupData.equipIdList.forEach { equip ->
                        EquipItem(
                            favoriteIdList = favoriteIdList,
                            equip = equip,
                            toEquipDetail = toEquipDetail,
                            toEquipMaterial = toEquipMaterial,
                            searchEquipMode = true,
                            searchEquipIdList = searchEquipIdList,
                            selectEquip = selectEquip
                        )
                    }
                }
            }

        }

        items(2) {
            CommonSpacer()
        }
    }
}


/**
 * 装备
 */
@Composable
private fun EquipItem(
    favoriteIdList: List<Int>,
    equip: EquipmentBasicInfo,
    toEquipDetail: (Int) -> Unit,
    toEquipMaterial: (Int, String) -> Unit,
    searchEquipMode: Boolean,
    searchEquipIdList: List<Int>,
    selectEquip: (Int) -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(defaultSpring())
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable {
                VibrateUtil(context).single()
                if (searchEquipMode) {
                    selectEquip(equip.equipmentId)
                } else {
                    //点击跳转
                    if (equip.craftFlg == 1) {
                        toEquipDetail(equip.equipmentId)
                    } else {
                        toEquipMaterial(equip.equipmentId, equip.equipmentName)
                    }
                }
            },
        horizontalArrangement = if (searchEquipMode) Arrangement.Center else Arrangement.Start
    ) {
        Box(contentAlignment = Alignment.Center) {
            MainIcon(
                data = ImageRequestHelper.getInstance()
                    .getUrl(ImageRequestHelper.ICON_EQUIPMENT, equip.equipmentId)
            )
            if (searchEquipMode && searchEquipIdList.contains(equip.equipmentId)) {
                SelectText(
                    selected = true,
                    text = stringResource(id = R.string.selected_mark),
                    margin = 0.dp
                )
            }
        }
        if (!searchEquipMode) {
            Subtitle2(
                text = equip.equipmentName,
                textAlign = TextAlign.Start,
                maxLines = 2,
                selectable = true,
                modifier = Modifier.padding(start = Dimen.smallPadding),
                color = if (favoriteIdList.contains(equip.equipmentId)) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
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
private fun EquipListContentPreview() {
    val name = stringResource(id = R.string.debug_short_text)
    PreviewLayout {
        EquipListContent(
            equipList = arrayListOf(
                EquipmentBasicInfo(equipmentId = 1, equipmentName = name, promotionLevel = 1),
                EquipmentBasicInfo(equipmentId = 2, equipmentName = name, promotionLevel = 2),
                EquipmentBasicInfo(equipmentId = 3, equipmentName = name, promotionLevel = 2),
                EquipmentBasicInfo(equipmentId = 4, equipmentName = name, promotionLevel = 2),
            ),
            scrollState = rememberLazyListState(),
            favoriteIdList = arrayListOf(1),
            toEquipDetail = { },
            toEquipMaterial = { _, _ -> },
            searchEquipMode = false,
            searchEquipIdList = arrayListOf(),
            selectEquip = {}
        )
    }
}

@CombinedPreviews
@Composable
private fun EquipSearchFabContentPreview() {
    PreviewLayout {
        EquipSearchFabContent(
            openSearchDialog = true,
            searchEquipMode = true,
            searchEquipIdList = arrayListOf(1, 2, 3, 4, 5),
            toSearchEquipQuest = {},
            changeSearchMode = {},
            selectEquip = {},
            changeSearchDialog = {}
        )
    }
}
