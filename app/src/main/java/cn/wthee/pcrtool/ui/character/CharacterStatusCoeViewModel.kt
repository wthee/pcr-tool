package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.UnitStatusCoefficient
import cn.wthee.pcrtool.ui.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：属性说明
 */
@Immutable
data class CharacterStatusCoeUiState(
    val coeValue: UnitStatusCoefficient? = null,
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 属性说明 ViewModel
 *
 * @param unitRepository
 *
 */
@HiltViewModel
class CharacterStatusCoeViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterStatusCoeUiState())
    val uiState: StateFlow<CharacterStatusCoeUiState> = _uiState.asStateFlow()

    init {
        getCoefficient()
    }


    /**
     * 获取战力系数
     */
    private fun getCoefficient() {
        viewModelScope.launch {
            val data = unitRepository.getCoefficient()
            _uiState.update {
                it.copy(
                    coeValue = data,
                    loadingState = it.loadingState.isSuccess(data != null)
                )
            }
        }
    }

}
