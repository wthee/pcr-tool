package cn.wthee.pcrtool.ui.tool.storyevent

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.view.StoryEventData
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.components.DateRange
import cn.wthee.pcrtool.ui.updateLoadState
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.compareAllTypeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：剧情活动
 */
@Immutable
data class StoryEventListUiState(
    //日期
    val dateRange: DateRange = DateRange(),
    //日期选择弹窗
    val openDialog: Boolean = false,
    //剧情活动列表
    val storyList: List<StoryEventData>? = null,
    val loadState: LoadState = LoadState.Loading
)

/**
 * 剧情活动 ViewModel
 *
 * @param eventRepository
 */
@HiltViewModel
class StoryEventListViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoryEventListUiState())
    val uiState: StateFlow<StoryEventListUiState> = _uiState.asStateFlow()

    /**
     * 日期选择更新
     */
    fun changeRange(dateRange: DateRange) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    dateRange = dateRange
                )
            }
        }
        getStoryEventHistory(dateRange)
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

    /**
     * 获取剧情活动记录
     */
    private fun getStoryEventHistory(dateRange: DateRange) {
        viewModelScope.launch {
            try {
                var list = eventRepository.getAllEvents(Int.MAX_VALUE)
                if (dateRange.hasFilter()) {
                    list = list.filter {
                        dateRange.predicate(it.startTime)
                    }
                }

                list = list.sortedWith(compareAllTypeEvent())

                _uiState.update {
                    it.copy(
                        storyList = list,
                        loadState = updateLoadState(list)
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getStoryEventHistory")
            }
        }

    }

    /**
     * 重置
     */
    fun reset() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    dateRange = DateRange()
                )
            }
        }
        getStoryEventHistory(DateRange())
    }
}
