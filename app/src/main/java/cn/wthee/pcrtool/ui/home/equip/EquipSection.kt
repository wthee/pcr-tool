package cn.wthee.pcrtool.ui.home.equip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.components.commonPlaceholder
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.spanCount
import kotlin.math.max


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

    val equipSpanCount = max(
        1,
        (Dimen.iconSize + Dimen.largePadding * 2).spanCount
    )

    LaunchedEffect(equipSpanCount) {
        equipSectionViewModel.loadData(equipSpanCount)
    }

    //装备总数
    val equipCount = uiState.equipCount



    Section(
        id = id,
        titleId = R.string.tool_equip,
        iconType = MainIconType.EQUIP,
        hintText = equipCount.toString(),
        contentVisible = equipCount > 0,
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
        VerticalGrid(
            itemWidth = Dimen.iconSize + Dimen.largePadding * 2
        ) {
            if (uiState.equipList?.isNotEmpty() == true) {
                uiState.equipList?.forEach {
                    val placeholder = it.equipmentId == ImageRequestHelper.UNKNOWN_EQUIP_ID
                    Box(
                        modifier = Modifier
                            .padding(Dimen.mediumPadding)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        MainIcon(
                            data = ImageRequestHelper.getInstance()
                                .getEquipPic(it.equipmentId),
                            modifier = Modifier.commonPlaceholder(placeholder)
                        ) {
                            if (!placeholder) {
                                toEquipDetail(it.equipmentId)
                            }
                        }
                    }
                }
            }
        }
    }
}