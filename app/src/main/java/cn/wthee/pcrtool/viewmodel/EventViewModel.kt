package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.ui.components.DateRange
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.compareAllTypeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 活动 ViewModel
 *
 * @param eventRepository
 */
@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    /**
     * 获取剧情活动记录
     */
    fun getStoryEventHistory(dateRange: DateRange) = flow {
        try {
            var list = eventRepository.getAllEvents(Int.MAX_VALUE)
            if (dateRange.hasFilter()) {
                list = list.filter {
                    dateRange.predicate(it.startTime)
                }
            }

            emit(list.sortedWith(compareAllTypeEvent()))
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getStoryEventHistory")
        }
    }

    /**
     * 获取免费十连活动记录
     */
    fun getFreeGachaHistory() = flow {
        try {
            emit(eventRepository.getFreeGachaEvent(Int.MAX_VALUE))
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getFreeGachaHistory")
        }
    }

    /**
     * 获取活动列表
     */
    fun getCalendarEventList(dateRange: DateRange) = flow {
        try {
            val data = eventRepository.getDropEvent(Int.MAX_VALUE).toMutableList()
            data += eventRepository.getMissionEvent(Int.MAX_VALUE)
            data += eventRepository.getLoginEvent(Int.MAX_VALUE)
            data += eventRepository.getFortuneEvent(Int.MAX_VALUE)
            data += eventRepository.getTowerEvent(Int.MAX_VALUE)
            data += eventRepository.getSpDungeonEvent(Int.MAX_VALUE)
            data += eventRepository.getFaultEvent(Int.MAX_VALUE)
            data += eventRepository.getColosseumEvent(Int.MAX_VALUE)

            var list = data.toList()
            if (dateRange.hasFilter()) {
                list = data.filter {
                    dateRange.predicate(it.startTime)
                }
            }

            emit(
                list.sortedWith(compareAllTypeEvent())
            )
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getCalendarEventList")
        }
    }
}
