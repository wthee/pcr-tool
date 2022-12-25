package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.CenterTipText
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.common.MainText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.travel.ExtraEquipSubGroup
import cn.wthee.pcrtool.utils.getRegionName
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel

@Composable
fun CharacterExtraEquip(
    scrollState: LazyListState,
    unitId: Int,
    toExtraEquipDetail: (Int) -> Unit,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {

    val equipList = extraEquipmentViewModel.getCharacterExtraEquipList(unitId).collectAsState(
        initial = null
    ).value

    Box(modifier = Modifier.fillMaxSize()) {
        if(equipList != null){
            if (equipList.isEmpty()) {
                CenterTipText(
                    stringResource(
                        id = R.string.no_data,
                        getRegionName(MainActivity.regionType)
                    )
                )
            } else {
                LazyColumn(state = scrollState) {
                    item {
                        //标题
                        MainText(
                            text = stringResource(R.string.unit_extra_equip_slot),
                            modifier = Modifier
                                .padding(Dimen.largePadding)
                                .fillMaxWidth()
                        )
                    }
                    items(equipList) {
                        ExtraEquipSubGroup(
                            it.category,
                            it.categoryName,
                            it.exEquipmentIds.intArrayList,
                            0,
                            toExtraEquipDetail
                        )
                    }
                    item {
                        CommonSpacer()
                    }
                }
            }
        }else {
            //功能未实装
            CenterTipText(
                stringResource(
                    id = R.string.not_installed,
                    getRegionName(MainActivity.regionType)
                )
            )
        }
    }
}