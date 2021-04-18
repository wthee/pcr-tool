package cn.wthee.pcrtool.ui.equip

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.data.view.allNotZero
import cn.wthee.pcrtool.ui.compose.AttrList
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.getEquipIconUrl
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel

/**
 * TODO 打开装备详情
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun OpenEquipDetail(
    equipId: Int,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    callCotent: @Composable (PaddingValues) -> Unit = {}
) {
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
//            EquipDetail(equipId)
        },
        sheetPeekHeight = 0.dp,
    ) {

    }
}

/**
 * 装备详情
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun EquipDetail(equipmentViewModel: EquipmentViewModel) {
    val equipId = equipmentViewModel.openEquipDetailDialog.observeAsState().value ?: -1

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()

    if (equipId != -1) {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                EquipMainInfo(equipId)
            },
            sheetPeekHeight = 0.dp
        ) {}
    }

}

/**
 * 装备详情
 */
@ExperimentalFoundationApi
@Composable
fun EquipMainInfo(equipId: Int, equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel()) {
    equipmentViewModel.getEquip(equipId)
    val equipMaxData = equipmentViewModel.equip.observeAsState().value
    equipMaxData?.let {
        Column {
            Text(text = it.equipmentName)
            Row(modifier = Modifier.fillMaxWidth()) {
                IconCompose(
                    data = getEquipIconUrl(equipId),
                    modifier = Modifier.size(Dimen.iconSize)
                )
                Text(text = it.getDesc())
            }
            AttrList(attrs = it.attr.allNotZero())
        }
    }
}