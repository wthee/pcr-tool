package cn.wthee.pcrtool.ui.tool.extraequip.unit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.ui.components.IconListContent

/**
 * 可使用的ex装备角色
 */
@Composable
fun ExtraEquipUnitListScreen(
    extraEquipUnitListViewModel: ExtraEquipUnitListViewModel = hiltViewModel()
) {
    val uiState by extraEquipUnitListViewModel.uiState.collectAsStateWithLifecycle()

    IconListContent(
        idList = uiState.unitIdList,
        title = stringResource(R.string.extra_equip_unit),
        iconResourceType = IconResourceType.CHARACTER
    )

}