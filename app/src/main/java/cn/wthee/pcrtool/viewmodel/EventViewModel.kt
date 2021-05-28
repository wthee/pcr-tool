package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.EventStoryDetail
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.view.EventData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    var events = MutableLiveData<List<EventData>>()
    var storys = MutableLiveData<List<EventStoryDetail>>()


    /**
     * 获取活动记录
     */
    fun getEventHistory() {
        viewModelScope.launch {
            val data = eventRepository.getAllEvents()
            events.postValue(data)
        }
    }
}
