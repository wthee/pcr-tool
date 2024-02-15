package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipmentBasicInfo
import cn.wthee.pcrtool.data.enums.ExtraEquipLevelColor
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ExtraEquipGroupData
import cn.wthee.pcrtool.data.model.FilterExtraEquipment
import cn.wthee.pcrtool.data.model.isFilter
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonGroupTitle
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.LifecycleEffect
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.VerticalGridList
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.VibrateUtil
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * ex装备列表
 */
@Composable
fun ExtraEquipList(
    extraEquipmentViewModel: ExtraEquipListViewModel = hiltViewModel(),
    toExtraEquipDetail: (Int) -> Unit,
    toFilterExtraEquip: (String) -> Unit,
) {
    val uiState by extraEquipmentViewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()

    //初始筛选信息
    LifecycleEffect(Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_CREATE) {
        extraEquipmentViewModel.initFilter()
    }


    MainScaffold(
        fab = {
            if (uiState.loadState == LoadState.Success) {
                ExtraEquipListFabContent(
                    count = uiState.equipList?.size ?: 0,
                    scrollState = scrollState,
                    filter = uiState.filter,
                    resetFilter = extraEquipmentViewModel::resetFilter,
                    toFilterExtraEquip = toFilterExtraEquip
                )
            }
        }
    ) {
        StateBox(
            stateType = uiState.loadState,
            errorContent = {
                CenterTipText(text = stringResource(R.string.not_installed))
            }
        ) {
            if (uiState.equipList != null && uiState.filter != null) {
                ExtraEquipListContent(
                    equipList = uiState.equipList!!,
                    favoriteIdList = uiState.favoriteIdList,
                    scrollState = scrollState,
                    toExtraEquipDetail = toExtraEquipDetail
                )
            }
        }
    }
}

@Composable
private fun ExtraEquipListContent(
    equipList: List<ExtraEquipmentBasicInfo>,
    favoriteIdList: List<Int>,
    scrollState: LazyListState,
    toExtraEquipDetail: (Int) -> Unit
) {
    val equipGroupList = arrayListOf<ExtraEquipGroupData>()
    equipList.forEach { equip ->
        var group = equipGroupList.find {
            it.rarity == equip.rarity && it.category == equip.category
        }
        if (group == null) {
            group = ExtraEquipGroupData(equip.rarity, equip.category, equip.categoryName)
            equipGroupList.add(group)
        }
        group.equipIdList.add(equip)
    }

    LazyColumn(state = scrollState) {
        items(
            items = equipGroupList,
            key = {
                "${it.rarity}-${it.category}"
            }
        ) { equipGroupData ->
            ExtraEquipGroup(
                equipGroupData = equipGroupData,
                favoriteIdList = favoriteIdList,
                toExtraEquipDetail = toExtraEquipDetail
            )
        }
        item {
            CommonSpacer()
        }
    }
}

@Composable
private fun ExtraEquipListFabContent(
    count: Int,
    scrollState: LazyListState,
    filter: FilterExtraEquipment?,
    resetFilter: () -> Unit,
    toFilterExtraEquip: (String) -> Unit
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
        iconType = MainIconType.EXTRA_EQUIP,
        text = "$count",
        onClick = {
            filter?.let {
                toFilterExtraEquip(Json.encodeToString(filter))
            }
        }
    )
}

/**
 * ex装备分组
 */
@Composable
private fun ExtraEquipGroup(
    equipGroupData: ExtraEquipGroupData,
    favoriteIdList: List<Int>,
    toExtraEquipDetail: (Int) -> Unit
) {
    //分组标题
    CommonGroupTitle(
        iconData = ImageRequestHelper.getInstance()
            .getUrl(
                ImageRequestHelper.ICON_EXTRA_EQUIPMENT_CATEGORY,
                equipGroupData.category
            ),
        iconSize = Dimen.smallIconSize,
        backgroundColor = ExtraEquipLevelColor.getByType(equipGroupData.rarity).color,
        titleStart = stringResource(
            id = R.string.extra_equip_rarity_and_type,
            equipGroupData.rarity,
            equipGroupData.categoryName
        ),
        titleEnd = equipGroupData.equipIdList.size.toString(),
        modifier = Modifier.padding(Dimen.mediumPadding)
    )

    //分组内容
    VerticalGridList(
        itemCount = equipGroupData.equipIdList.size,
        itemWidth = Dimen.iconSize * 3,
        contentPadding = Dimen.mediumPadding,
        modifier = Modifier.padding(
            horizontal = Dimen.commonItemPadding
        )
    ) {
        ExtraEquipItem(
            favoriteIdList = favoriteIdList,
            equip = equipGroupData.equipIdList[it],
            toExtraEquipDetail = toExtraEquipDetail
        )
    }
}

/**
 * ex装备
 */
@Composable
private fun ExtraEquipItem(
    favoriteIdList: List<Int>,
    equip: ExtraEquipmentBasicInfo,
    toExtraEquipDetail: (Int) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable {
                VibrateUtil(context).single()
                toExtraEquipDetail(equip.equipmentId)
            }
    ) {
        MainIcon(
            data = ImageRequestHelper.getInstance()
                .getUrl(ImageRequestHelper.ICON_EXTRA_EQUIPMENT, equip.equipmentId)
        )
        MainContentText(
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

@CombinedPreviews
@Composable
private fun ExtraEquipGroupPreview() {
    val text = stringResource(id = R.string.debug_short_text)
    PreviewLayout {
        ExtraEquipGroup(
            ExtraEquipGroupData(
                rarity = 3,
                category = 1,
                categoryName = text,
                equipIdList = arrayListOf(
                    ExtraEquipmentBasicInfo(equipmentId = 1, equipmentName = text),
                    ExtraEquipmentBasicInfo(equipmentName = text),
                    ExtraEquipmentBasicInfo(equipmentName = text)
                )
            ),
            favoriteIdList = arrayListOf(1)
        ) { }
    }
}