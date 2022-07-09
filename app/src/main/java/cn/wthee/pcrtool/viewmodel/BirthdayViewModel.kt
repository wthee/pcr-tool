package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.utils.compareBirthDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 生日日程信息
 */
@HiltViewModel
class BirthdayViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : ViewModel() {

    /**
     * 获取生日
     */
    fun getBirthDayList() = flow {
        try {
            val data = eventRepository.getBirthdayList().sortedWith(compareBirthDay())
            emit(data)
        } catch (_: Exception) {

        }
    }

}