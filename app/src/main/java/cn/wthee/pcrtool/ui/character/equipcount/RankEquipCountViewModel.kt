package cn.wthee.pcrtool.ui.character.equipcount

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.data.model.FilterEquip
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.updateLoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：角色装备统计
 */
@Immutable
data class RankEquipCountUiState(
    val rank0: Int = 0,
    val rank1: Int = 0,
    val maxRank: Int = 0,
    val equipmentMaterialList: List<EquipmentMaterial>? = null,
    val unitId: Int = 0,
    val isAllUnit: Boolean = false,
    //收藏信息
    val favoriteIdList: List<Int> = emptyList(),
    val loadState: LoadState = LoadState.Loading,
    val openDialog: Boolean = false
)

/**
 * 角色装备统计 ViewModel
 *
 */
@HiltViewModel
class RankEquipCountViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val unitId: Int = savedStateHandle[NavRoute.UNIT_ID] ?: 0

    private val _uiState = MutableStateFlow(RankEquipCountUiState())
    val uiState: StateFlow<RankEquipCountUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (_uiState.value.maxRank == 0) {
                val maxRank = equipmentRepository.getMaxRank()
                _uiState.update {
                    it.copy(
                        unitId = unitId,
                        rank0 = maxRank,
                        rank1 = maxRank,
                        maxRank = maxRank,
                        isAllUnit = unitId == 0
                    )
                }
                getEquipByRank()
            }
        }
    }

    /**
     * 更新 rank
     */
    fun updateRank(rank0: Int, rank1: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    rank0 = rank0,
                    rank1 = rank1
                )
            }
            getEquipByRank()
        }
    }


    /**
     * 根据角色id [unitId] 获取对应 Rank 范围 所需的装备
     */
    private fun getEquipByRank() {
        viewModelScope.launch {
            val data = equipmentRepository.getEquipByRank(
                unitId = _uiState.value.unitId,
                startRank = _uiState.value.rank0,
                endRank = _uiState.value.rank1,
            )
            _uiState.update {
                it.copy(
                    equipmentMaterialList = data,
                    loadState = updateLoadState(data)
                )
            }
        }
    }

    /**
     * 获取收藏列表
     */
    fun reloadFavoriteList() {
        viewModelScope.launch {
            val list = FilterEquip.getFavoriteIdList()
            _uiState.update {
                it.copy(favoriteIdList = list)
            }
        }
    }

    /**
     * 弹窗状态更新
     */
    fun changeDialog(openDialog: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openDialog = openDialog
                )
            }
        }
    }
}
