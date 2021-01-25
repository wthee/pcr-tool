package cn.wthee.pcrtool.ui.tool.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.view.EventData
import kotlinx.coroutines.launch

/**
 * 活动 ViewModel
 *
 * 数据来源 [EventRepository]
 */
class EventViewModel(
    private val repository: EventRepository
) : ViewModel() {

    var events = MutableLiveData<List<EventData>>()


    /**
     * 获取活动记录
     */
    fun getEventHistory() {
        viewModelScope.launch {
            val data = repository.getAllEvents()
            events.postValue(data)
        }
    }

}
