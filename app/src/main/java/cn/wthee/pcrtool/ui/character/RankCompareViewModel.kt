package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.model.AttrCompareData
import cn.wthee.pcrtool.navigation.NavRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：角色 RANK 对比
 */
@Immutable
data class RankCompareUiState(
    val rank0: Int = 0,
    val rank1: Int = 0,
    val maxRank: Int = 0,
    val attrCompareDataList: List<AttrCompareData> = emptyList(),
    val unitId: Int = 0,
    val level: Int = 0,
    val rarity: Int = 0,
    val uniqueEquipLevel: Int = 0,
    val uniqueEquipLevel2: Int = 0,
)

/**
 * 角色 RANK 对比 ViewModel
 *
 * @param unitRepository
 *
 */
@HiltViewModel
class RankCompareViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val unitId: Int? = savedStateHandle[NavRoute.UNIT_ID]
    private val maxRank: Int? = savedStateHandle[NavRoute.MAX_RANK]
    private val level: Int? = savedStateHandle[NavRoute.LEVEL]
    private val rarity: Int? = savedStateHandle[NavRoute.RARITY]
    private val uniqueEquipLevel: Int? = savedStateHandle[NavRoute.UNIQUE_EQUIP_LEVEL]
    private val uniqueEquipLevel2: Int? = savedStateHandle[NavRoute.UNIQUE_EQUIP_LEVEL2]

    private val _uiState = MutableStateFlow(RankCompareUiState())
    val uiState: StateFlow<RankCompareUiState> = _uiState.asStateFlow()

    init {
        if (unitId != null && level != null && rarity != null && uniqueEquipLevel != null
            && uniqueEquipLevel2 != null && maxRank != null
        ) {
            _uiState.update {
                it.copy(
                    unitId = unitId,
                    level = level,
                    rarity = rarity,
                    uniqueEquipLevel = uniqueEquipLevel,
                    uniqueEquipLevel2 = uniqueEquipLevel2,
                    rank0 = maxRank,
                    rank1 = maxRank,
                    maxRank = maxRank,
                )
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
        }
        getUnitAttrCompare()
    }

    /**
     * 获取指定角色属性对比
     */
    private fun getUnitAttrCompare() {
        viewModelScope.launch {
            val attr0 = unitRepository.getAttrs(
                _uiState.value.unitId,
                _uiState.value.level,
                _uiState.value.rank0,
                _uiState.value.rarity,
                _uiState.value.uniqueEquipLevel,
                _uiState.value.uniqueEquipLevel2
            )
            val attr1 = unitRepository.getAttrs(
                _uiState.value.unitId,
                _uiState.value.level,
                _uiState.value.rank1,
                _uiState.value.rarity,
                _uiState.value.uniqueEquipLevel,
                _uiState.value.uniqueEquipLevel2
            )
            _uiState.update {
                it.copy(
                    attrCompareDataList = attr0.sumAttr.compareWith(attr1.sumAttr)
                )
            }
        }
    }

}
