package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.ui.components.UnitList

/**
 * 可使用的ex装备角色
 */
@Composable
fun ExtraEquipUnitListScreen(
    extraEquipUnitListViewModel: ExtraEquipUnitListViewModel = hiltViewModel()
) {
    val uiState by extraEquipUnitListViewModel.uiState.collectAsStateWithLifecycle()

    UnitList(uiState.unitIdList)
}