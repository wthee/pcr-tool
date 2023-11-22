package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterExtraEquipData
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.tool.extratravel.ExtraEquipGroup
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 角色额外装备
 */
@Composable
fun CharacterExtraEquipScreen(
    toExtraEquipDetail: (Int) -> Unit,
    characterExtraEquipViewModel: CharacterExtraEquipViewModel = hiltViewModel()
) {
    val uiState by characterExtraEquipViewModel.uiState.collectAsStateWithLifecycle()

    MainScaffold {
        StateBox(
            stateType = uiState.loadingState
        ) {
            CharacterExtraEquipContent(uiState.extraEquipList, toExtraEquipDetail)
        }
    }

}

@Composable
private fun CharacterExtraEquipContent(
    equipList: List<CharacterExtraEquipData>?,
    toExtraEquipDetail: (Int) -> Unit,
) {
    val scrollState = rememberLazyListState()

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
        equipList?.let {
            items(equipList) {
                ExtraEquipGroup(
                    it.category,
                    it.categoryName,
                    it.exEquipmentIds.intArrayList,
                    selectedId = 0,
                    toExtraEquipDetail = toExtraEquipDetail
                )
            }
        }
        item {
            CommonSpacer()
        }
    }
}


@CombinedPreviews
@Composable
private fun CharacterExtraEquipContentPreview() {
    PreviewLayout {
        CharacterExtraEquipContent(
            equipList = arrayListOf(
                CharacterExtraEquipData(exEquipmentIds = "1-2"),
                CharacterExtraEquipData(exEquipmentIds = "1-2")
            ),
            toExtraEquipDetail = {},
        )
    }
}