package cn.wthee.pcrtool.ui.tool.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.EventRepository
import cn.wthee.pcrtool.data.entity.EventStoryData
import kotlinx.coroutines.launch


class EventViewModel(
    private val repository: EventRepository
) : ViewModel() {

    var events = MutableLiveData<List<EventStoryData>>()
    var isLoading = MutableLiveData<Boolean>()


    //活动
    fun getEventHistory() {
        isLoading.postValue(true)
        viewModelScope.launch {
            val data = repository.getAllEvents()
            isLoading.postValue(false)
            events.postValue(data)
        }
    }

}
