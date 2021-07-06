package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.compare
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.second
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页纵览
 */
@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    private val equipmentRepository: EquipmentRepository,
    private val eventRepository: EventRepository,
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    /**
     * 获取角色列表
     */
    fun getCharacterList(size: Int) = flow {
        emit(unitRepository.getInfoAndData(size))
    }

    /**
     * 获取装备列表
     */
    fun getEquipList() = flow {
        emit(equipmentRepository.getEquipments(10))
    }

    /**
     * 获取活动列表
     *
     * @param type 0：进行中 1：预告
     */
    fun getCalendarEventList(type: Int) = flow {
        val today = getToday()
        val data = eventRepository.getDropEvent() + eventRepository.getTowerEvent(1)
        if (type == 0) {
            emit(data.filter {
                val sd = it.startTime.formatTime
                val ed = it.endTime.formatTime
                val inProgress = today.second(sd) > 0 && ed.second(today) > 0
                inProgress
            }.sortedWith(compare(today)))
        } else {
            emit(data.filter {
                val sd = it.startTime.formatTime
                val comingSoon = today.second(sd) < 0
                comingSoon
            }.sortedWith(compare(today)))
        }
    }

    /**
     * 获取新闻
     */
    fun getNewsOverview() = flow {
        val data = apiRepository.getNewsOverview().data
        data?.let {
            emit(it)
        }
    }

    /**
     * 六星 id 列表
     */
    fun getR6Ids() {
        viewModelScope.launch {
            MainActivity.navViewModel.r6Ids.postValue(unitRepository.getR6Ids())
        }
    }
}