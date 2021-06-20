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
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.second
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
    var inProgressEventList = MutableLiveData<List<CalendarEvent>>()
    var comingSoonEventList = MutableLiveData<List<CalendarEvent>>()
    var newsList = MutableLiveData<ResponseData<List<NewsTable>>>()


    /**
     * 获取角色列表
     */
    fun getCharacterList() {
        viewModelScope.launch {
            if (characterList.value == null) {
                val data = unitRepository.getInfoAndData(6)
                characterList.postValue(data)
            }
        }
    }

    /**
     * 获取装备列表
     */
    fun getEquipList() {
        viewModelScope.launch {
            if (equipList.value == null) {
                val data = equipmentRepository.getEquipments(10)
                equipList.postValue(data)
            }
        }
    }

    /**
     * 获取活动列表
     */
    fun getCalendarEventList() {
        viewModelScope.launch {
            if (inProgressEventList.value == null || comingSoonEventList.value == null) {
                val data0 = eventRepository.getDropEvent()
                val data1 = eventRepository.getTowerEvent(1)
                //按进行中排序
                val today = getToday()
                val list0 = (data0 + data1).filter {
                    val sd = it.startTime.formatTime()
                    val ed = it.endTime.formatTime()
                    val inProgress = today.second(sd) > 0 && ed.second(today) > 0
                    inProgress
                }.sortedWith(compare(today))
                val list1 = (data0 + data1).filter {
                    val sd = it.startTime.formatTime()
                    val comingSoon = today.second(sd) < 0
                    comingSoon
                }.sortedWith(compare(today))
                inProgressEventList.postValue(list0)
                comingSoonEventList.postValue(list1)
            }
        }
    }

    /**
     * 获取新闻
     */
    fun getNewsOverview() {
        viewModelScope.launch {
            if (newsList.value == null) {
                val data = apiRepository.getNewsOverview()
                newsList.postValue(data)
            }
        }
    }

    /**
     * 六星 id 列表
     */
    suspend fun getR6Ids() = unitRepository.getR6Ids()
}