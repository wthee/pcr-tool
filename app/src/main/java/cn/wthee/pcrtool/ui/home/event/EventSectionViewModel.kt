package cn.wthee.pcrtool.ui.home.event

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.data.db.view.BirthdayData
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.ClanBattleEvent
import cn.wthee.pcrtool.data.db.view.FreeGachaInfo
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.data.db.view.StoryEventData
import cn.wthee.pcrtool.data.db.view.endTime
import cn.wthee.pcrtool.data.db.view.startTime
import cn.wthee.pcrtool.data.enums.EventType
import cn.wthee.pcrtool.utils.compareAllTypeEvent
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.utils.isComingSoon
import cn.wthee.pcrtool.utils.isInProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：日程纵览
 */
@Immutable
data class EventSectionUiState(
    //预告掉落活动
    val comingSoonEventList: List<CalendarEvent> = emptyList(),
    //预告剧情活动
    val comingSoonStoryEventList: List<StoryEventData> = emptyList(),
    //预告卡池
    val comingSoonGachaList: List<GachaInfo> = emptyList(),
    //预告免费十连
    val comingSoonFreeGachaList: List<FreeGachaInfo> = emptyList(),
    //预告生日
    val comingSoonBirthdayList: List<BirthdayData> = emptyList(),
    //预告公会战
    val comingSoonClanBattleList: List<ClanBattleEvent> = emptyList(),
    //进行中掉落活动
    val inProgressEventList: List<CalendarEvent> = emptyList(),
    //进行中剧情活动
    val inProgressStoryEventList: List<StoryEventData> = emptyList(),
    //进行中卡池
    val inProgressGachaList: List<GachaInfo> = emptyList(),
    //进行中免费十连
    val inProgressFreeGachaList: List<FreeGachaInfo> = emptyList(),
    //进行中生日
    val inProgressBirthdayList: List<BirthdayData> = emptyList(),
    //进行中公会战
    val inProgressClanBattleList: List<ClanBattleEvent> = emptyList(),
    //fes角色id
    val fesUnitIdList: List<Int> = emptyList(),
)

/**
 * 日程纵览
 */
@HiltViewModel
class EventSectionViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val gachaRepository: GachaRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventSectionUiState())
    val uiState: StateFlow<EventSectionUiState> = _uiState.asStateFlow()


    fun loadData(type: EventType) {
        getCalendarEventList(type)
        getStoryEventList(type)
        getGachaList(type)
        getFreeGachaList(type)
        getBirthdayList(type)
        getClanBattleEvent(type)
        getGachaFesUnitList()
    }


    /**
     * 获取免费十连卡池列表
     */
    private fun getFreeGachaList(type: EventType) {
        viewModelScope.launch {
            val today = getToday()
            val data = eventRepository.getFreeGachaEvent(1)

            _uiState.update { state ->
                if (type == EventType.IN_PROGRESS) {
                    state.copy(
                        inProgressFreeGachaList = data.filter {
                            isInProgress(today, it.startTime, it.endTime)
                        }
                    )
                } else {
                    state.copy(
                        comingSoonFreeGachaList = data.filter {
                            isComingSoon(today, it.startTime)
                        }
                    )
                }
            }
        }
    }

    /**
     * 获取生日日程列表
     */
    private fun getBirthdayList(type: EventType) {
        viewModelScope.launch {
            val today = getToday()
            val data = eventRepository.getBirthdayList()

            _uiState.update { state ->
                if (type == EventType.IN_PROGRESS) {
                    var list = data.filter {
                        isInProgress(today, it.startTime, it.endTime)
                    }
                    //只取一条记录
                    if (list.isNotEmpty()) {
                        list = list.subList(0, 1)
                    }
                    state.copy(
                        inProgressBirthdayList = list
                    )
                } else {
                    var list = data.filter {
                        isComingSoon(today, it.startTime)
                    }
                    //只取一条记录
                    if (list.isNotEmpty()) {
                        list = list.subList(0, 1)
                    }
                    state.copy(
                        comingSoonBirthdayList = list
                    )
                }
            }
        }
    }

    /**
     * 获取最新公会战日程
     */
    private fun getClanBattleEvent(type: EventType) {
        viewModelScope.launch {
            val today = getToday()
            val data = eventRepository.getClanBattleEvent(2)

            _uiState.update { state ->
                if (type == EventType.IN_PROGRESS) {
                    state.copy(
                        inProgressClanBattleList = data.filter {
                            isInProgress(today, it.startTime, it.getFixedEndTime())
                        }
                    )
                } else {
                    state.copy(
                        comingSoonClanBattleList = data.filter {
                            isComingSoon(today, it.startTime)
                        }
                    )
                }
            }
        }
    }

    /**
     * 获取卡池列表
     */
    private fun getGachaList(type: EventType) {
        viewModelScope.launch {
            val today = getToday()
            //数据库时间格式问题，导致查询不出最新的，先多查询一些数据
            val data = gachaRepository.getGachaHistory(200)

            _uiState.update { state ->
                if (type == EventType.IN_PROGRESS) {
                    state.copy(
                        inProgressGachaList = data.filter {
                            isInProgress(today, it.startTime, it.endTime)
                        }.sortedWith(compareAllTypeEvent(today))
                    )
                } else {
                    state.copy(
                        comingSoonGachaList = data.filter {
                            isComingSoon(today, it.startTime)
                        }.sortedWith(compareAllTypeEvent(today))
                    )
                }
            }
        }
    }

    /**
     * 获取活动列表
     */
    private fun getCalendarEventList(type: EventType) {
        viewModelScope.launch {
            val today = getToday()
            val data = eventRepository.getDropEvent(50).toMutableList()
            data += eventRepository.getMissionEvent(1)
            data += eventRepository.getLoginEvent(1)
            data += eventRepository.getFortuneEvent(1)
            data += eventRepository.getTowerEvent(1)
            data += eventRepository.getSpDungeonEvent(1)
            data += eventRepository.getFaultEvent(1)
            data += eventRepository.getColosseumEvent(1)

            _uiState.update { state ->
                if (type == EventType.IN_PROGRESS) {
                    state.copy(
                        inProgressEventList = data.filter {
                            isInProgress(today, it.startTime, it.endTime)
                        }.sortedWith(compareAllTypeEvent(today))
                    )
                } else {
                    state.copy(
                        comingSoonEventList = data.filter {
                            isComingSoon(today, it.startTime)
                        }.sortedWith(compareAllTypeEvent(today))
                    )
                }
            }
        }
    }

    /**
     * 获取剧情活动列表
     */
    private fun getStoryEventList(type: EventType) {
        viewModelScope.launch {
            val today = getToday()
            val data = eventRepository.getAllEvents(10)

            _uiState.update { state ->
                if (type == EventType.IN_PROGRESS) {
                    state.copy(
                        inProgressStoryEventList = data.filter {
                            isInProgress(today, it.startTime, it.endTime)
                        }.sortedWith(compareAllTypeEvent(today))
                    )
                } else {
                    state.copy(
                        comingSoonStoryEventList = data.filter {
                            isComingSoon(today, it.startTime)
                        }.sortedWith(compareAllTypeEvent(today))
                    )
                }
            }
        }
    }


    /**
     * 获取卡池fes角色
     */
    private fun getGachaFesUnitList() {
        viewModelScope.launch {
            val data = gachaRepository.getFesUnitIdList()?.unitIds?.intArrayList ?: emptyList()
            _uiState.update {
                it.copy(
                    fesUnitIdList = data
                )
            }
        }
    }
}