package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.UnitStatusCoefficient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：角色战力系数
 */
@Immutable
data class CharacterCoeUiState(
    val coeValue: UnitStatusCoefficient? = null,
)

/**
 * 角色面板属性 ViewModel
 *
 * @param unitRepository
 *
 */
@HiltViewModel
class CharacterCoeViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterCoeUiState())
    val uiState: StateFlow<CharacterCoeUiState> = _uiState.asStateFlow()

    init {
        getCoefficient()
    }
    /**
     * 获取战力系数
     */
    private fun getCoefficient()  {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    coeValue = unitRepository.getCoefficient()
                )
            }
        }
    }

}
