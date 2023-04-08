package cn.wthee.pcrtool.ui.tool

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.ui.character.RankEquipCount
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel

/**
 * 全角色装备需求统计
 */
@Composable
fun AllCharacterRankEquipCount(
    toEquipMaterial: (Int) -> Unit,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    val maxRank = equipmentViewModel.getMaxRank().collectAsState(initial = 0).value

    if (maxRank != 0) {
        RankEquipCount(
            0,
            maxRank,
            toEquipMaterial,
            isAllUnit = true
        )
    }
}