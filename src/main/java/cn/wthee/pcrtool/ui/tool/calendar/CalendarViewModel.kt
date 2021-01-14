package cn.wthee.pcrtool.ui.tool.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.network.model.CalendarData
import cn.wthee.pcrtool.data.network.model.ResponseData
import kotlinx.coroutines.launch


class CalendarViewModel : ViewModel() {

    val calendar = MutableLiveData<ResponseData<CalendarData>>()

    fun getCalendar() {
        viewModelScope.launch {
            val data = MyAPIRepository.getCalendar()
            calendar.postValue(data)
        }
    }

}
