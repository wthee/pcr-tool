package cn.wthee.pcrtool.ui.home.equip

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
 * 装备预览
 */
@Composable
fun EquipSection(
    updateOrderData: (Int) -> Unit,
    toEquipList: () -> Unit,
    toEquipDetail: (Int) -> Unit,
    isEditMode: Boolean,
    orderStr: String,
    equipSectionViewModel: EquipSectionViewModel = hiltViewModel()
) {
    val uiState by equipSectionViewModel.uiState.collectAsStateWithLifecycle()

    EquipSectionContent(
        uiState = uiState,
        isEditMode = isEditMode,
        orderStr = orderStr,
        loadData = equipSectionViewModel::loadData,
        updateOrderData = updateOrderData,
        toEquipList = toEquipList,
        toEquipDetail = toEquipDetail
    )
}

@Composable
private fun EquipSectionContent(
    uiState: EquipSectionUiState,
    isEditMode: Boolean,
    orderStr: String,
    loadData: (Int) -> Unit,
    updateOrderData: (Int) -> Unit,
    toEquipList: () -> Unit,
    toEquipDetail: (Int) -> Unit
) {
    val id = OverviewType.EQUIP.id
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
        titleId = R.string.tool_equip,
        iconType = MainIconType.EQUIP,
        hintText = uiState.equipCount.toString(),
        contentVisible = uiState.equipCount > 0,
        isEditMode = isEditMode,
        orderStr = orderStr,
        onClick = {
            if (isEditMode) {
                updateOrderData(id)
            } else {
                toEquipList()
            }
        }
    ) {
        GridIconList(
            paddingValues = PaddingValues(top = Dimen.mediumPadding),
            idList = uiState.equipIdList,
            iconResourceType = IconResourceType.EQUIP,
            itemWidth = Dimen.homeIconItemWidth,
            contentPadding = 0.dp,
            fixCount = equipSpanCount,
            onClickItem = toEquipDetail
        )
    }
}


@CombinedPreviews
@Composable
private fun EquipSectionContentPreview() {
    PreviewLayout {
        EquipSectionContent(
            uiState = EquipSectionUiState(
                equipCount = 100,
                equipIdList = arrayListOf(1, 2, 3, 4, 5)
            ),
            isEditMode = false,
            orderStr = "${OverviewType.EQUIP.id}",
            loadData = { },
            updateOrderData = { },
            toEquipList = {},
            toEquipDetail = {}
        )
    }
}