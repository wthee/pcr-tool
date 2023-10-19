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
import cn.wthee.pcrtool.data.db.view.UniqueEquipBasicData
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
 * 专用装备预览
 */
@Composable
fun UniqueEquipSection(
    actions: NavActions,
    isEditMode: Boolean,
    orderStr: String,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val id = OverviewType.UNIQUE_EQUIP.id
    val uniqueEquipSpanCount = max(
        1,
        (Dimen.iconSize + Dimen.largePadding * 2).spanCount
    )
    //装备总数
    val uniqueEquipCountFlow = remember {
        overviewViewModel.getUniqueEquipCount()
    }
    val uniqueEquipCount by uniqueEquipCountFlow.collectAsState(initial = "0")

    val initList = arrayListOf<UniqueEquipBasicData>()
    for (i in 1..uniqueEquipSpanCount) {
        initList.add(UniqueEquipBasicData())
    }

    //专用装备1
    val equipList1Flow = remember(uniqueEquipSpanCount, 1) {
        overviewViewModel.getUniqueEquipList(uniqueEquipSpanCount, 1)
    }
    val equipList1 by equipList1Flow.collectAsState(initial = initList)
    //专用装备2
    val equipList2Flow = remember(uniqueEquipSpanCount, 2) {
        overviewViewModel.getUniqueEquipList(uniqueEquipSpanCount, 2)
    }
    val equipList2 by equipList2Flow.collectAsState(initial = arrayListOf())


    Section(
        id = id,
        titleId = R.string.tool_unique_equip,
        iconType = MainIconType.UNIQUE_EQUIP,
        hintText = uniqueEquipCount,
        contentVisible = uniqueEquipCount != "0",
        isEditMode = isEditMode,
        orderStr = orderStr,
        onClick = {
            if (isEditMode)
                editOverviewMenuOrder(id){
                    overviewViewModel.overviewOrderData.postValue(it)
                }
            else
                actions.toUniqueEquipList()
        }
    ) {
        VerticalGrid(
            itemWidth = Dimen.iconSize + Dimen.largePadding * 2
        ) {
            equipList1.forEach {
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
                            actions.toUniqueEquipDetail(it.unitId)
                        }
                    }
                }
            }
        }

        VerticalGrid(
            itemWidth = Dimen.iconSize + Dimen.largePadding * 2
        ) {
            equipList2.forEach {
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
                            actions.toUniqueEquipDetail(it.unitId)
                        }
                    }
                }
            }
        }
    }
}