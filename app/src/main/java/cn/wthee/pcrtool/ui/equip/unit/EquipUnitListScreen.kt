package cn.wthee.pcrtool.ui.equip.unit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.ui.components.IconListContent
import cn.wthee.pcrtool.ui.components.IconListContentPreview
import cn.wthee.pcrtool.ui.theme.CombinedPreviews

/**
 * 可使用装备角色列表
 */
@Composable
fun EquipUnitListScreen(
    equipUnitListViewModel: EquipUnitListViewModel = hiltViewModel()
) {
    val uiState by equipUnitListViewModel.uiState.collectAsStateWithLifecycle()


    IconListContent(
        idList = uiState.unitIdList,
        title = stringResource(R.string.extra_equip_unit),
        iconResourceType = IconResourceType.CHARACTER
    )
}

@CombinedPreviews
@Composable
private fun Preview() {
    IconListContentPreview()
}