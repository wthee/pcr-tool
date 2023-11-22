package cn.wthee.pcrtool.ui.tool

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.view.BirthdayData
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
 * 页面状态：生日
 */
@Immutable
data class BirthdayListUiState(
    val birthdayList: List<BirthdayData> = emptyList(),
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 生日日程信息
 */
@HiltViewModel
class BirthdayListViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BirthdayListUiState())
    val uiState: StateFlow<BirthdayListUiState> = _uiState.asStateFlow()

    init {
        getBirthDayList()
    }

    /**
     * 获取生日
     */
    private fun getBirthDayList() {
        viewModelScope.launch {
            val list = eventRepository.getBirthdayList()
            _uiState.update {
                it.copy(
                    birthdayList = list,
                    loadingState = updateLoadingState(list)
                )
            }
        }
    }

}