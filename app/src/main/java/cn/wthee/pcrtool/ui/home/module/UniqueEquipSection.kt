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
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.home.editOverviewMenuOrder
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.spanCount
import cn.wthee.pcrtool.viewmodel.OverviewViewModel
import kotlin.math.max


/**
 * 专用装备预览
 */
@Composable
fun UniqueEquipSection(
    actions: NavActions,
    isEditMode: Boolean,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val id = OverviewType.UNIQUE_EQUIP.id
    val equipSpanCount = max(
        1,
        (Dimen.iconSize + Dimen.largePadding * 2).spanCount
    )
    //装备总数
    val equipCount = overviewViewModel.getUniqueEquipCount().collectAsState(initial = 0).value
    //装备列表
    val equipList = overviewViewModel.getUniqueEquipList(equipSpanCount)
        .collectAsState(initial = arrayListOf()).value

    Section(
        id = id,
        titleId = R.string.tool_unique_equip,
        iconType = MainIconType.UNIQUE_EQUIP,
        hintText = equipCount.toString(),
        contentVisible = equipList.isNotEmpty(),
        isEditMode = isEditMode,
        onClick = {
            if (isEditMode)
                editOverviewMenuOrder(id)
            else
                actions.toUniqueEquipList()
        }
    ) {
        VerticalGrid(
            itemWidth = Dimen.iconSize + Dimen.largePadding * 2
        ) {
            if (equipList.isNotEmpty()) {
                equipList.forEach {
                    Box(
                        modifier = Modifier
                            .padding(Dimen.mediumPadding)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        MainIcon(
                            data = ImageRequestHelper.getInstance()
                                .getEquipPic(it.equipId)
                        ) {
                            actions.toUniqueEquipDetail(it.unitId)
                        }
                    }
                }
            }
        }
    }
}