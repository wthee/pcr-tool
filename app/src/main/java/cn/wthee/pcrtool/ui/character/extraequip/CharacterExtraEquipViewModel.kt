package cn.wthee.pcrtool.ui.character.extraequip

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ExtraEquipmentRepository
import cn.wthee.pcrtool.data.db.view.CharacterExtraEquipData
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
 * 页面状态：角色适用ex装备
 */
@Immutable
data class CharacterExtraEquipUiState(
    val extraEquipList: List<CharacterExtraEquipData>? = null,
    val loadState: LoadState = LoadState.Loading
)

/**
 * 角色适用ex装备 ViewModel
 *
 * @param extraEquipmentRepository
 *
 */
@HiltViewModel
class CharacterExtraEquipViewModel @Inject constructor(
    private val extraEquipmentRepository: ExtraEquipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val unitId: Int? = savedStateHandle[NavRoute.UNIT_ID]

    private val _uiState = MutableStateFlow(CharacterExtraEquipUiState())
    val uiState: StateFlow<CharacterExtraEquipUiState> = _uiState.asStateFlow()

    init {
        if (unitId != null) {
            getCharacterExtraEquipList(unitId)
        }
    }


    /**
     * 获取角色可使用的ex装备列表
     */
    private fun getCharacterExtraEquipList(unitId: Int) {
        viewModelScope.launch {
            val list = extraEquipmentRepository.getCharacterExtraEquipList(unitId)
            _uiState.update {
                it.copy(
                    extraEquipList = list,
                    loadState = updateLoadState(list)
                )
            }
        }
    }

}
