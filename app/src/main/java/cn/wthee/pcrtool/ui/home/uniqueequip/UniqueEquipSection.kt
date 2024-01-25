package cn.wthee.pcrtool.ui.home.uniqueequip

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.UniqueEquipBasicData
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.ui.components.GridIconList
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.spanCount


/**
 * 专用装备预览
 */
@Composable
fun UniqueEquipSection(
    updateOrderData: (Int) -> Unit,
    toUniqueEquipList: () -> Unit,
    toUniqueEquipDetail: (Int) -> Unit,
    isEditMode: Boolean,
    orderStr: String,
    uniqueEquipSectionViewModel: UniqueEquipSectionViewModel = hiltViewModel()
) {
    val uiState by uniqueEquipSectionViewModel.uiState.collectAsStateWithLifecycle()

    UniqueEquipSectionContent(
        uiState = uiState,
        isEditMode = isEditMode,
        orderStr = orderStr,
        loadData = uniqueEquipSectionViewModel::loadData,
        updateOrderData = updateOrderData,
        toUniqueEquipList = toUniqueEquipList,
        toUniqueEquipDetail = toUniqueEquipDetail
    )
}

@Composable
private fun UniqueEquipSectionContent(
    uiState: UniqueEquipSectionUiState,
    isEditMode: Boolean,
    orderStr: String,
    loadData: (Int) -> Unit,
    updateOrderData: (Int) -> Unit,
    toUniqueEquipList: () -> Unit,
    toUniqueEquipDetail: (Int) -> Unit
) {
    val id = OverviewType.UNIQUE_EQUIP.id
    val context = LocalContext.current
    var equipSpanCount by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(equipSpanCount) {
        loadData(equipSpanCount)
    }

    Section(
        modifier = Modifier.onSizeChanged {
            equipSpanCount = spanCount(
                width = it.width,
                itemDp = Dimen.homeIconItemWidth,
                context = context
            )
        },
        id = id,
        titleId = R.string.tool_unique_equip,
        iconType = MainIconType.UNIQUE_EQUIP,
        hintText = uiState.uniqueEquipCount,
        contentVisible = uiState.uniqueEquipCount != "0",
        isEditMode = isEditMode,
        orderStr = orderStr,
        onClick = {
            if (isEditMode) {
                updateOrderData(id)
            } else {
                toUniqueEquipList()
            }
        }
    ) {
        uiState.uniqueEquipList1?.let { list ->
            GridIconList(
                paddingValues = PaddingValues(top = Dimen.mediumPadding),
                idList = list.map { it.equipId },
                detailIdList = list.map { it.unitId },
                iconResourceType = IconResourceType.UNIQUE_EQUIP,
                itemWidth = Dimen.homeIconItemWidth,
                fixCount = equipSpanCount,
                contentPadding = 0.dp,
                onClickItem = toUniqueEquipDetail
            )
        }
        uiState.uniqueEquipList2?.let { list ->
            GridIconList(
                paddingValues = PaddingValues(top = Dimen.mediumPadding),
                idList = list.map { it.equipId },
                detailIdList = list.map { it.unitId },
                iconResourceType = IconResourceType.UNIQUE_EQUIP,
                itemWidth = Dimen.homeIconItemWidth,
                fixCount = equipSpanCount,
                contentPadding = 0.dp,
                onClickItem = toUniqueEquipDetail
            )
        }
    }
}


@CombinedPreviews
@Composable
private fun UniqueEquipSectionContentPreview() {
    PreviewLayout {
        val list = arrayListOf(
            UniqueEquipBasicData(),
            UniqueEquipBasicData(),
            UniqueEquipBasicData(),
            UniqueEquipBasicData(),
            UniqueEquipBasicData(),
        )

        UniqueEquipSectionContent(
            uiState = UniqueEquipSectionUiState(
                uniqueEquipCount = "100",
                uniqueEquipList1 = list,
                uniqueEquipList2 = list
            ),
            isEditMode = false,
            orderStr = "${OverviewType.UNIQUE_EQUIP.id}",
            loadData = { },
            updateOrderData = { },
            toUniqueEquipList = {},
            toUniqueEquipDetail = {}
        )
    }
}