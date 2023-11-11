package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ExtraEquipmentRepository
import cn.wthee.pcrtool.navigation.NavRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：ex装备适用角色列表
 */
@Immutable
data class ExtraEquipUnitListUiState(
    //适用角色
    val unitIdList: List<Int> = emptyList(),
)

/**
 * ex装备适用角色列表 ViewModel
 */
@HiltViewModel
class ExtraEquipUnitListViewModel @Inject constructor(
    private val extraEquipmentRepository: ExtraEquipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val category: Int? = savedStateHandle[NavRoute.EXTRA_EQUIP_CATEGORY]

    private val _uiState = MutableStateFlow(ExtraEquipUnitListUiState())
    val uiState: StateFlow<ExtraEquipUnitListUiState> = _uiState.asStateFlow()


    init {
        if (category != null) {
            getExtraEquipUnitList(category)
        }
    }

    /**
     * 获取可使用装备的角色列表
     */
    private fun getExtraEquipUnitList(category: Int) {
        viewModelScope.launch {
            val list = extraEquipmentRepository.getEquipUnitList(category)
            _uiState.update {
                it.copy(
                    unitIdList = list
                )
            }
        }
    }


}

