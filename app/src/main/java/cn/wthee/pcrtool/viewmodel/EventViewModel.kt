package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.utils.compareEvent
import cn.wthee.pcrtool.utils.compareStoryEvent
import cn.wthee.pcrtool.utils.getToday
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
     * 获取活动记录
     */
    fun getStoryEventHistory() = flow {
        try {
            emit(eventRepository.getAllEvents(Int.MAX_VALUE).sortedWith(compareStoryEvent()))
        } catch (e: Exception) {

        }
    }

    /**
     * 获取免费十连活动记录
     */
    fun getFreeGachaHistory() = flow {
        try {
            emit(eventRepository.getFreeGachaEvent(Int.MAX_VALUE))
        } catch (e: Exception) {

        }
    }

    /**
     * 获取活动列表
     */
    fun getCalendarEventList() = flow {
        try {
            val data = eventRepository.getDropEvent() + eventRepository.getTowerEvent(1)
            emit(
                data.sortedWith(compareEvent())
            )
        } catch (e: Exception) {

        }

    }
}
