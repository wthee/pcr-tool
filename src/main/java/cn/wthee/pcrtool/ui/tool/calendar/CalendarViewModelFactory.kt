package cn.wthee.pcrtool.ui.tool.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.data.db.repository.EventRepository

class CalendarViewModelFactory(
    private val repository: EventRepository
) : ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalendarViewModel(
            repository
        ) as T
    }
}