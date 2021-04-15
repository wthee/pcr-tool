package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.model.CalendarData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.view.DropEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 日历 ViewModel
 *
 * 数据来源 [MyAPIRepository] [EventRepository]
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    val calendar = MutableLiveData<ResponseData<CalendarData>>()
    val dropEvents = MutableLiveData<List<DropEvent>>()

    /**
     * 获取日历数据
     */
    fun getCalendar() {
        viewModelScope.launch {
            val data = MyAPIRepository.getInstance().getCalendar()
            calendar.postValue(data)
        }
    }


    /**
     * 获取加倍活动信息
     */
    fun getDropEvent() {
        viewModelScope.launch {
            val data = eventRepository.getDropEvent()
            dropEvents.postValue(data)
        }
    }

}
