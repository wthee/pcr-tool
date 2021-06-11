package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.compare
import cn.wthee.pcrtool.utils.getToday
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : ViewModel() {

    var characterList = MutableLiveData<List<CharacterInfo>>()
    var equipList = MutableLiveData<List<EquipmentMaxData>>()
    var eventList = MutableLiveData<List<CalendarEvent>>()


    /**
     * 获取角色列表
     */
    fun getCharacterList() {
        viewModelScope.launch {
            var data = unitRepository.getInfoAndData(10)
            if (data.isEmpty()) {
                data = arrayListOf(CharacterInfo())
            }
            characterList.postValue(data)
        }
    }

    /**
     * 获取装备列表
     */
    fun getEquipList() {
        viewModelScope.launch {
            var data = equipmentRepository.getEquipments(10)
            if (data.isEmpty()) {
                data = arrayListOf(EquipmentMaxData())
            }
            equipList.postValue(data)
        }
    }

    /**
     * 获取活动列表
     */
    fun getCalendarEventList() {
        viewModelScope.launch {
            val data0 = eventRepository.getDropEvent()
            val data1 = eventRepository.getTowerEvent(1)
            //按进行中排序
            val today = getToday()
            eventList.postValue((data0 + data1).sortedWith(compare(today)).subList(0, 5))
        }
    }
}