package cn.wthee.pcrtool.ui.tool.event

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.enums.CalendarEventType
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
data class CalendarEventListUiState(
    //日期
    val dateRange: DateRange = DateRange(),
    //日期选择弹窗
    val openDatePickDialog: Boolean = false,
    //类型选择弹窗
    val openTypeDialog: Boolean = false,
    //类型筛选
    val eventType: CalendarEventType = CalendarEventType.ALL,
    //活动列表
    val calendarEventList: List<CalendarEvent>? = null,
    val loadState: LoadState = LoadState.Loading
)

/**
 * 剧情活动 ViewModel
 *
 * @param eventRepository
 */
@HiltViewModel
class CalendarEventListViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarEventListUiState())
    val uiState: StateFlow<CalendarEventListUiState> = _uiState.asStateFlow()

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
        getCalendarEventList(dateRange, _uiState.value.eventType)
    }

    /**
     * 日期弹窗状态更新
     */
    fun changeDatePickDialog(openDialog: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openDatePickDialog = openDialog
                )
            }
        }
    }


    /**
     * 类型弹窗状态更新
     */
    fun changeTypeDialog(openDialog: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openTypeDialog = openDialog
                )
            }
        }
    }

    /**
     * 获取活动列表
     */
    private fun getCalendarEventList(dateRange: DateRange, type: CalendarEventType) {
        viewModelScope.launch {
            try {
                val data = arrayListOf<CalendarEvent>()
                if (type == CalendarEventType.ALL || type == CalendarEventType.ABYSS) {
                    data += eventRepository.getAbyssEvent(Int.MAX_VALUE)
                }
                if (type == CalendarEventType.ALL || type == CalendarEventType.COLOSSEUM) {
                    data += eventRepository.getColosseumEvent(Int.MAX_VALUE)
                }
                if (type == CalendarEventType.ALL || type == CalendarEventType.SP_DUNGEON) {
                    data += eventRepository.getSpDungeonEvent(Int.MAX_VALUE)
                }
                if (type == CalendarEventType.ALL || type == CalendarEventType.TDF) {
                    data += eventRepository.getFaultEvent(Int.MAX_VALUE)
                }
                if (type == CalendarEventType.ALL) {
                    data += eventRepository.getLoginEvent(Int.MAX_VALUE)
                    data += eventRepository.getFortuneEvent(Int.MAX_VALUE)
                    data += eventRepository.getTowerEvent(Int.MAX_VALUE)
                    data += eventRepository.getDropEvent(Int.MAX_VALUE)
                    data += eventRepository.getMissionEvent(Int.MAX_VALUE)
                }
                

                var list = data.toList()
                if (dateRange.hasFilter()) {
                    list = data.filter {
                        dateRange.predicate(it.startTime, it.endTime)
                    }
                }

                list = list.sortedWith(compareAllTypeEvent())

                _uiState.update {
                    it.copy(
                        calendarEventList = list,
                        loadState = updateLoadState(list)
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getCalendarEventList")
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
        getCalendarEventList(dateRange = DateRange(), type = CalendarEventType.ALL)
    }

    /**
     * 切换活动类型
     */
    fun changeTypeSelect(index: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    eventType = CalendarEventType.getByIndex(index)
                )
            }
        }
        getCalendarEventList(dateRange = _uiState.value.dateRange, type = _uiState.value.eventType)
    }

}
