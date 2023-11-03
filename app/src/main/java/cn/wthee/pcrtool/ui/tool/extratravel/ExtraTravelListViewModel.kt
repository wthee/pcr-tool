package cn.wthee.pcrtool.ui.tool.extratravel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ExtraEquipmentRepository
import cn.wthee.pcrtool.data.db.view.ExtraTravelData
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
 * 页面状态：ex冒险区域
 */
@Immutable
data class ExtraTravelListUiState(
    val areaList: List<ExtraTravelData>? = null,
    val loadingState: LoadingState = LoadingState.Loading
)


/**
 * ex冒险区域 ViewModel
 */
@HiltViewModel
class ExtraTravelListViewModel @Inject constructor(
    private val extraEquipmentRepository: ExtraEquipmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExtraTravelListUiState())
    val uiState: StateFlow<ExtraTravelListUiState> = _uiState.asStateFlow()

    init {
        getTravelAreaList()
    }

    /**
     * ex冒险区域
     */
    private fun getTravelAreaList() {
        viewModelScope.launch {
            val list = extraEquipmentRepository.getTravelAreaList()
            _uiState.update {
                it.copy(
                    areaList = list,
                    loadingState = updateLoadingState(list)
                )
            }
        }
    }
}
