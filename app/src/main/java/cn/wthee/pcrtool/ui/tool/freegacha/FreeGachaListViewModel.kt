package cn.wthee.pcrtool.ui.tool.freegacha

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.view.FreeGachaInfo
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
 * 页面状态：生日
 */
@Immutable
data class FreeGachaListUiState(
    val freeGachaList: List<FreeGachaInfo> = emptyList(),
    val loadState: LoadState = LoadState.Loading
)

/**
 * 生日日程信息
 */
@HiltViewModel
class FreeGachaListViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FreeGachaListUiState())
    val uiState: StateFlow<FreeGachaListUiState> = _uiState.asStateFlow()

    init {
        getFreeGachaHistory()
    }

    /**
     * 获取免费十连活动记录
     */
    private fun getFreeGachaHistory() {
        viewModelScope.launch {
            val list = eventRepository.getFreeGachaEvent(Int.MAX_VALUE)
            _uiState.update {
                it.copy(
                    freeGachaList = list,
                    loadState = updateLoadState(list)
                )
            }
        }
    }
}