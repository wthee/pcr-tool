package cn.wthee.pcrtool.ui.tool.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.EventRepository
import cn.wthee.pcrtool.data.view.EventData
import kotlinx.coroutines.launch


class EventViewModel(
    private val repository: EventRepository
) : ViewModel() {

    var events = MutableLiveData<List<EventData>>()


    //活动
    fun getEventHistory() {
        viewModelScope.launch {
            val data = repository.getAllEvents()
            events.postValue(data)
        }
    }

}
