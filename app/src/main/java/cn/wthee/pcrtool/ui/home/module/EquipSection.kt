package cn.wthee.pcrtool.ui.home.module

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentBasicInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.components.commonPlaceholder
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.home.editOverviewMenuOrder
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.spanCount
import cn.wthee.pcrtool.viewmodel.OverviewViewModel
import kotlin.math.max


/**
 * 装备预览
 */
@Composable
fun EquipSection(
    actions: NavActions,
    isEditMode: Boolean,
    orderStr: String,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val id = OverviewType.EQUIP.id
    val equipSpanCount = max(
        1,
        (Dimen.iconSize + Dimen.largePadding * 2).spanCount
    )
    //装备总数
    val equipCountFlow = remember {
        overviewViewModel.getEquipCount()
    }
    val equipCount by equipCountFlow.collectAsState(initial = 0)
    //装备列表
    val equipListFlow = remember(equipSpanCount) {
        overviewViewModel.getEquipList(equipSpanCount * 2)
    }
    val initList = arrayListOf<EquipmentBasicInfo>()
    for (i in 1..equipSpanCount * 2) {
        initList.add(EquipmentBasicInfo())
    }
    val equipList by equipListFlow.collectAsState(initial = initList)


    Section(
        id = id,
        titleId = R.string.tool_equip,
        iconType = MainIconType.EQUIP,
        hintText = equipCount.toString(),
        contentVisible = equipCount > 0,
        isEditMode = isEditMode,
        orderStr = orderStr,
        onClick = {
            if (isEditMode)
                editOverviewMenuOrder(id)
            else
                actions.toEquipList()
        }
    ) {
        VerticalGrid(
            itemWidth = Dimen.iconSize + Dimen.largePadding * 2
        ) {
            if (equipList.isNotEmpty()) {
                equipList.forEach {
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
                                actions.toEquipDetail(it.equipmentId)
                            }
                        }
                    }
                }
            }
        }
    }
}