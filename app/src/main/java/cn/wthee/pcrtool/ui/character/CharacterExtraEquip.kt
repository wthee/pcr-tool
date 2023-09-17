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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.extratravel.ExtraEquipGroup
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

    val equipListFlow = remember(unitId) {
        extraEquipmentViewModel.getCharacterExtraEquipList(unitId)
    }
    val equipList by equipListFlow.collectAsState(initial = null)

    Box(modifier = Modifier.fillMaxSize()) {
        equipList?.let { list ->
            if (list.isEmpty()) {
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
                    items(list) {
                        ExtraEquipGroup(
                            it.category,
                            it.categoryName,
                            it.exEquipmentIds.intArrayList,
                            selectedId = 0,
                            toExtraEquipDetail = toExtraEquipDetail
                        )
                    }
                    item {
                        CommonSpacer()
                    }
                }
            }
        }

        if (equipList == null) {
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