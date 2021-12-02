package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.compare
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.settingSP
import cn.wthee.pcrtool.utils.*
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
    fun getCharacterList() = flow {
        emit(unitRepository.getInfoAndData(FilterCharacter(), "全部"))
    }

    /**
     * 获取装备列表
     */
    fun getEquipList() = flow {
        emit(equipmentRepository.getEquipments(FilterEquipment(), "全部"))
    }

    /**
     * 获取活动列表
     *
     * @param type 0：进行中 1：预告
     */
    fun getCalendarEventList(type: Int) = flow {
        val regionType = settingSP(MyApplication.context).getInt(Constants.SP_DATABASE_TYPE, 2)
        val today = getToday()
        val data = eventRepository.getDropEvent() + eventRepository.getTowerEvent(1)

        if (type == 0) {
            emit(data.filter {
                val sd = fixJpTime(it.startTime.formatTime, regionType)
                val ed = fixJpTime(it.endTime.formatTime, regionType)
                val inProgress = today.second(sd) > 0 && ed.second(today) > 0
                inProgress
            }.sortedWith(compare(today)))
        } else {
            emit(data.filter {
                val sd = fixJpTime(it.startTime.formatTime, regionType)
                val comingSoon = today.second(sd) < 0
                comingSoon
            }.sortedWith(compare(today)))
        }
    }

    /**
     * 获取新闻
     */
    fun getNewsOverview(region: Int) = flow {
        val data = apiRepository.getNewsOverviewByRegion(region).data
        data?.let {
            emit(it)
        }
    }

    /**
     * 六星 id 列表
     */
    fun getR6Ids() {
        viewModelScope.launch {
            navViewModel.r6Ids.postValue(unitRepository.getR6Ids())
        }
    }
}