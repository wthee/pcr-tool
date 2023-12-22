package cn.wthee.pcrtool.ui.home.uniqueequip

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
import cn.wthee.pcrtool.data.db.view.UniqueEquipBasicData
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
    val id = OverviewType.UNIQUE_EQUIP.id
    val uiState by uniqueEquipSectionViewModel.uiState.collectAsStateWithLifecycle()

    val equipSpanCount = max(
        1,
        (Dimen.iconSize + Dimen.largePadding * 2).spanCount
    )

    LaunchedEffect(equipSpanCount) {
        uniqueEquipSectionViewModel.loadData(equipSpanCount)
    }

    //装备总数
    val uniqueEquipCount = uiState.uniqueEquipCount
    //装备列表
    val initList = arrayListOf<UniqueEquipBasicData>()
    for (i in 1..equipSpanCount) {
        initList.add(UniqueEquipBasicData())
    }
    val equipList1 = if (uiState.uniqueEquipList1 == null) {
        initList
    } else {
        uiState.uniqueEquipList1
    }

    val equipList2 = uiState.uniqueEquipList2


    Section(
        id = id,
        titleId = R.string.tool_unique_equip,
        iconType = MainIconType.UNIQUE_EQUIP,
        hintText = uniqueEquipCount,
        contentVisible = uniqueEquipCount != "0",
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
        VerticalGrid(
            itemWidth = Dimen.iconSize + Dimen.largePadding * 2
        ) {
            equipList1?.forEach {
                val placeholder = it.equipId == ImageRequestHelper.UNKNOWN_EQUIP_ID
                Box(
                    modifier = Modifier
                        .padding(Dimen.mediumPadding)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MainIcon(
                        data = ImageRequestHelper.getInstance()
                            .getEquipPic(it.equipId),
                        modifier = Modifier.commonPlaceholder(placeholder)
                    ) {
                        if (!placeholder) {
                            toUniqueEquipDetail(it.unitId)
                        }
                    }
                }
            }
        }

        VerticalGrid(
            itemWidth = Dimen.iconSize + Dimen.largePadding * 2
        ) {
            equipList2?.forEach {
                val placeholder = it.equipId == ImageRequestHelper.UNKNOWN_EQUIP_ID
                Box(
                    modifier = Modifier
                        .padding(Dimen.mediumPadding)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MainIcon(
                        data = ImageRequestHelper.getInstance()
                            .getEquipPic(it.equipId),
                        modifier = Modifier.commonPlaceholder(placeholder)
                    ) {
                        if (!placeholder) {
                            toUniqueEquipDetail(it.unitId)
                        }
                    }
                }
            }
        }
    }
}