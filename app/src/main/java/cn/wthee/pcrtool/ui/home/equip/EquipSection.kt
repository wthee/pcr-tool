package cn.wthee.pcrtool.ui.home.equip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.ui.components.GridIconList
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.dp2px
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
    val id = OverviewType.EQUIP.id
    val uiState by equipSectionViewModel.uiState.collectAsStateWithLifecycle()

    val equipSpanCount = spanCount(
        LocalView.current.width - (Dimen.mediumPadding * 2).value.dp2px,
        Dimen.homeIconItemWidth
    )

    LaunchedEffect(equipSpanCount) {
        equipSectionViewModel.loadData(equipSpanCount)
    }


    Section(
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
        uiState.equipIdList?.let {
            GridIconList(
                idList = it,
                iconResourceType = IconResourceType.EQUIP,
                itemWidth = Dimen.homeIconItemWidth,
                contentPadding = 0.dp,
                onClickItem = toEquipDetail
            )
        }
    }
}