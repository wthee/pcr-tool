package cn.wthee.pcrtool.ui.character.rankequip

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.view.UnitPromotion
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.navigation.setData
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.updateLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：角色Rank装备列表
 */
@Immutable
data class RankEquipListUiState(
    val currentRank: Int = 0,
    val rankEquipList: List<UnitPromotion>? = emptyList(),
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 角色Rank装备列表 ViewModel
 *
 */
@HiltViewModel
class RankEquipListViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val unitId: Int? = savedStateHandle[NavRoute.UNIT_ID]
    private val currentRank: Int? = savedStateHandle[NavRoute.RANK]

    private val _uiState = MutableStateFlow(RankEquipListUiState())
    val uiState: StateFlow<RankEquipListUiState> = _uiState.asStateFlow()

    init {
        if (unitId != null && currentRank != null) {
            getAllRankEquipList(unitId)
            updateCurrentRank(currentRank)
        }
    }

    /**
     * 获取角色 [unitId] 所有 RANK 装备列表
     *
     * @param unitId 角色编号
     */
    private fun getAllRankEquipList(unitId: Int) {
        viewModelScope.launch {
            val list = equipmentRepository.getRankEquipList(unitId)
            _uiState.update {
                it.copy(
                    rankEquipList = list,
                    loadingState = updateLoadingState(list)
                )
            }
        }
    }

    /**
     * 更新选中的Rank
     */
    fun updateCurrentRank(rank: Int) {
        _uiState.update {
            it.copy(
                currentRank = rank
            )
        }
        setData(NavRoute.RANK, rank, prev = true)
    }
}
