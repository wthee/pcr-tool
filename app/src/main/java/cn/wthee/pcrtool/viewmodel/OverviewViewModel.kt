package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.compare
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
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
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    var characterList = MutableLiveData<List<CharacterInfo>>()
    var equipList = MutableLiveData<List<EquipmentMaxData>>()
    var eventList = MutableLiveData<List<CalendarEvent>>()
    var newsList = MutableLiveData<ResponseData<List<NewsTable>>>()


    /**
     * 获取角色列表
     */
    fun getCharacterList() {
        viewModelScope.launch {
            val data = unitRepository.getInfoAndData(10)
            characterList.postValue(data)
        }
    }

    /**
     * 获取装备列表
     */
    fun getEquipList() {
        viewModelScope.launch {
            val data = equipmentRepository.getEquipments(10)
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
            val data = data0 + data1
            if (data.size > 4) {
                eventList.postValue(data.sortedWith(compare(today)).subList(0, 3))
            }
        }
    }

    /**
     * 获取新闻
     */
    fun getNewsOverview() {
        viewModelScope.launch {
            val data = apiRepository.getNewsOverview()
            newsList.postValue(data)
        }
    }

    /**
     * 六星 id 列表
     */
    suspend fun getR6Ids() = unitRepository.getR6Ids()
}