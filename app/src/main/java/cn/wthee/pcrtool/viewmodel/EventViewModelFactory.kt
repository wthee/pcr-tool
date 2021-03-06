package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.data.db.repository.EventRepository

class EventViewModelFactory(
    private val repository: EventRepository
) : ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EventViewModel(
            repository
        ) as T
    }
}