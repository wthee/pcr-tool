package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.compare
import cn.wthee.pcrtool.utils.getToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 日历 ViewModel
 *
 * @param eventRepository
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    val events = MutableLiveData<List<CalendarEvent>>()

    /**
     * 获取加倍活动信息
     */
    fun getDropEvent() {
        viewModelScope.launch {
            val data0 = eventRepository.getDropEvent()
            val data1 = eventRepository.getTowerEvent(10)
            //按进行中排序
            val today = getToday()
            events.postValue((data0 + data1).sortedWith(compare(today)))
        }
    }


}
