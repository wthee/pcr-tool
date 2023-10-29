package cn.wthee.pcrtool.ui.equip

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.navigation.NavRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：装备适用角色
 */
@Immutable
data class EquipUnitListUiState(

    //适用角色
    val unitIdList: List<Int> = emptyList(),
)

/**
 * 装备适用角色 ViewModel
 *
 * @param equipmentRepository
 * @param questRepository
 */
@HiltViewModel
class EquipUnitListViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val equipId: Int? = savedStateHandle[NavRoute.EQUIP_ID]

    private val _uiState = MutableStateFlow(EquipUnitListUiState())
    val uiState: StateFlow<EquipUnitListUiState> = _uiState.asStateFlow()

    init {
        if (equipId != null) {
            getEquipUnitList(equipId)
        }
    }

    /**
     * 获取装备适用角色
     */
    private fun getEquipUnitList(equipId: Int) {
        viewModelScope.launch {
            val unitIds = equipmentRepository.getEquipUnitList(equipId)
            _uiState.update {
                it.copy(
                    unitIdList = unitIds
                )
            }
        }
    }
}
