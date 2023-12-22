package cn.wthee.pcrtool.ui.equip.unit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.ui.components.UnitList

/**
 * 可使用装备角色列表
 */
@Composable
fun EquipUnitListScreen(
    equipUnitListViewModel: EquipUnitListViewModel = hiltViewModel()
) {
    val uiState by equipUnitListViewModel.uiState.collectAsStateWithLifecycle()


    UnitList(uiState.unitIdList)
}