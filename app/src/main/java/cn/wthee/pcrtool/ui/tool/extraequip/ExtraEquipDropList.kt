package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.common.CenterTipText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.travel.TravelQuestItem
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel

/**
 * ex装备掉落信息
 */
@Composable
fun ExtraEquipDropList(
    equipId: Int,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {
    val dropList = extraEquipmentViewModel.getDropQuestList(equipId)
        .collectAsState(initial = arrayListOf()).value

    Box {
        if (dropList.isNotEmpty()) {
            LazyColumn {
                items(dropList) {
                    TravelQuestItem(equipId, it, extraEquipmentViewModel)
                }
            }
        } else {
            Column(
                modifier = Modifier.defaultMinSize(minHeight = Dimen.minSheetHeight),
            ) {
                CenterTipText(text = stringResource(id = R.string.extra_equip_no_drop_quest))
            }
        }
    }
}
