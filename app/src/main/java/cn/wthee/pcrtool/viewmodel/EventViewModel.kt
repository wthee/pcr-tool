package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.entity.EventStoryDetail
import cn.wthee.pcrtool.data.view.EventData
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
    var storys = MutableLiveData<List<EventStoryDetail>>()


    /**
     * 获取活动记录
     */
    fun getEventHistory() {
        viewModelScope.launch {
            val data = repository.getAllEvents()
            events.postValue(data)
        }
    }

    /**
     * 获取活动剧情记录
     */
    fun getStoryDetails(storyId: Int) {
        viewModelScope.launch {
            val data = repository.getStoryDetails(storyId)
            storys.postValue(data)
        }
    }
}
