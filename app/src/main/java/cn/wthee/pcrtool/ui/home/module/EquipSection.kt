package cn.wthee.pcrtool.ui.home.module

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.IconCompose
import cn.wthee.pcrtool.ui.common.VerticalGrid
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.home.editOverviewMenuOrder
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.spanCount
import cn.wthee.pcrtool.viewmodel.OverviewViewModel


/**
 * 装备预览
 */
@Composable
fun EquipSection(
    actions: NavActions,
    isEditMode: Boolean,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val id = OverviewType.EQUIP.id
    val equipSpanCount = (Dimen.iconSize + Dimen.largePadding * 2).spanCount
    //装备总数
    val equipCount = overviewViewModel.getEquipCount().collectAsState(initial = 0).value
    //装备列表
    val equipList = overviewViewModel.getEquipList(maxOf(1, equipSpanCount) * 2)
        .collectAsState(initial = arrayListOf()).value

    Section(
        id = id,
        titleId = R.string.tool_equip,
        iconType = MainIconType.EQUIP,
        hintText = equipCount.toString(),
        contentVisible = equipList.isNotEmpty(),
        isEditMode = isEditMode,
        onClick = {
            if (isEditMode)
                editOverviewMenuOrder(id)
            else
                actions.toEquipList()
        }
    ) {
        VerticalGrid(
            spanCount = maxOf(1, equipSpanCount),
            modifier = Modifier.padding(horizontal = Dimen.commonItemPadding)
        ) {
            if (equipList.isNotEmpty()) {
                equipList.forEach {
                    Box(
                        modifier = Modifier
                            .padding(Dimen.mediumPadding)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconCompose(
                            data = ImageResourceHelper.getInstance()
                                .getEquipPic(it.equipmentId)
                        ) {
                            actions.toEquipDetail(it.equipmentId)
                        }
                    }
                }
            }
        }
    }
}