package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.view.DropEvent
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.hourInt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.Comparator

/**
 * 日历 ViewModel
 *
 * 数据来源 [MyAPIRepository] [EventRepository]
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    val dropEvents = MutableLiveData<List<DropEvent>>()

    /**
     * 获取加倍活动信息
     */
    fun getDropEvent() {
        viewModelScope.launch {
            val data = eventRepository.getDropEvent()
            //按进行中排序
            val cal = Calendar.getInstance()
            cal.time = Date(System.currentTimeMillis())
            val today = getToday()
            dropEvents.postValue(data.sortedWith(compare(today)))
        }
    }

    /**
     * 排序
     */
    private fun compare(today: String) = Comparator<DropEvent> { o1, o2 ->
        val sd1 = o1.startTime.formatTime()
        val ed1 = o1.endTime.formatTime()
        val sd2 = o2.startTime.formatTime()
        val ed2 = o2.endTime.formatTime()
        if (today.hourInt(sd1) > 0 && ed1.hourInt(today) > 0) {
            if (today.hourInt(sd2) > 0 && ed2.hourInt(today) > 0) {
                //都是进行中，比较结束时间
                ed2.compareTo(ed1)
            } else {
                //o1进行中
                -1
            }
        } else {
            if (today.hourInt(sd2) > 0 && ed2.hourInt(today) > 0) {
                //o2进行中
                1
            } else {
                //不是进行中
                if (sd1.hourInt(today) > 0) {
                    if (sd2.hourInt(today) > 0) {
                        //即将举行
                        sd1.compareTo(sd2)
                    } else {
                        -1
                    }
                } else {
                    if (sd2.hourInt(today) > 0) {
                        //即将举行
                        1
                    } else {
                        sd2.compareTo(sd1)
                    }
                }
            }
        }
    }

}
