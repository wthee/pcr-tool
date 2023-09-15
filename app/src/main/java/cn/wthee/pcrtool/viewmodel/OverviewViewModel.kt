package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.endTime
import cn.wthee.pcrtool.data.db.view.startTime
import cn.wthee.pcrtool.data.enums.EventType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.compareAllTypeEvent
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.isComingSoon
import cn.wthee.pcrtool.utils.isInProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

/**
 * 首页纵览
 */
@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    private val equipmentRepository: EquipmentRepository,
    private val eventRepository: EventRepository,
    private val gachaRepository: GachaRepository,
    private val apiRepository: MyAPIRepository
) : ViewModel() {
    val newOverview = MutableLiveData<ResponseData<List<NewsTable>>>()

    /**
     * 获取角色数量
     */
    fun getCharacterCount() = flow {
        try {
            emit(unitRepository.getCount())
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getCharacterCount")
        }
    }

    /**
     * 获取角色列表
     */
    fun getCharacterInfoList() = flow {
        try {
            val filterList = unitRepository.getCharacterInfoList(FilterCharacter(), 50)
            emit(filterList.subList(0, 10))
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getCharacterInfoList")
        }
    }

    /**
     * 获取装备数量
     */
    fun getEquipCount() = flow {
        try {
            emit(equipmentRepository.getCount())
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getEquipCount")
        }
    }

    /**
     * 获取装备列表
     */
    fun getEquipList(limit: Int) = flow {
        try {
            emit(equipmentRepository.getEquipments(FilterEquipment(), limit))
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getEquipList")
        }
    }

    /**
     * 获取专用装备数量
     */
    fun getUniqueEquipCount() = flow {
        try {
            emit(equipmentRepository.getUniqueEquipCount())
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getEquipCount")
        }
    }

    /**
     * 获取专用装备列表
     */
    fun getUniqueEquipList(limit: Int, slot: Int) = flow {
        try {
            val list = equipmentRepository.getUniqueEquipList("", slot)
            emit(list.subList(0, min(limit, list.size)))
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getEquipList")
        }
    }

    /**
     * 获取卡池列表
     */
    fun getGachaList(type: EventType) = flow {
        try {
            val today = getToday()
            //数据库时间格式问题，导致查询不出最新的，先多查询一些数据
            val data = gachaRepository.getGachaHistory(200)

            if (type == EventType.IN_PROGRESS) {
                emit(
                    data.filter {
                        isInProgress(today, it.startTime, it.endTime)
                    }.sortedWith(compareAllTypeEvent(today))
                )
            } else {
                emit(
                    data.filter {
                        isComingSoon(today, it.startTime)
                    }.sortedWith(compareAllTypeEvent(today))
                )
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getGachaList")
        }
    }

    /**
     * 获取活动列表
     */
    fun getCalendarEventList(type: EventType) = flow {
        try {
            val today = getToday()
            val data = eventRepository.getDropEvent().toMutableList()
            data += eventRepository.getMissionEvent(1)
            data += eventRepository.getLoginEvent(1)
            data += eventRepository.getFortuneEvent(1)
            data += eventRepository.getTowerEvent(1)
            data += eventRepository.getSpDungeonEvent(1)
            data += eventRepository.getFaultEvent(1)
            data += eventRepository.getColosseumEvent(1)

            if (type == EventType.IN_PROGRESS) {
                emit(
                    data.filter {
                        isInProgress(today, it.startTime, it.endTime)
                    }.sortedWith(compareAllTypeEvent(today))
                )
            } else {
                emit(
                    data.filter {
                        isComingSoon(today, it.startTime)
                    }.sortedWith(compareAllTypeEvent(today))
                )
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getCalendarEventList#type:$type")
        }

    }

    /**
     * 获取剧情活动列表
     */
    fun getStoryEventList(type: EventType) = flow {
        try {
            val today = getToday()
            val data = eventRepository.getAllEvents(10)

            if (type == EventType.IN_PROGRESS) {
                emit(
                    data.filter {
                        isInProgress(today, it.startTime, it.endTime)
                    }.sortedWith(compareAllTypeEvent(today))
                )
            } else {
                emit(
                    data.filter {
                        isComingSoon(today, it.startTime)
                    }.sortedWith(compareAllTypeEvent(today))
                )
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getStoryEventList#type:$type")
        }
    }

    /**
     * 获取新闻
     */
    fun getNewsOverview() {
        viewModelScope.launch {
            try {
                val data = apiRepository.getNewsOverviewByRegion(MainActivity.regionType.value)
                newOverview.postValue(data)
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getNewsOverview")
            }
        }
    }

    /**
     * 六星 id 列表
     */
    fun getR6Ids() {
        viewModelScope.launch {
            try {
                navViewModel.r6Ids.postValue(unitRepository.getR6Ids())
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getR6Ids")
            }
        }
    }

    /**
     * 获取免费十连卡池列表
     */
    fun getFreeGachaList(type: EventType) = flow {
        try {
            val today = getToday()
            val data = eventRepository.getFreeGachaEvent(1)

            if (type == EventType.IN_PROGRESS) {
                emit(data.filter {
                    isInProgress(today, it.startTime, it.endTime)
                })
            } else {
                emit(data.filter {
                    isComingSoon(today, it.startTime)
                })
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getFreeGachaList#type:$type")
        }
    }

    /**
     * 获取生日日程列表
     */
    fun getBirthdayList(type: EventType) = flow {
        try {
            val today = getToday()
            val data = eventRepository.getBirthdayList().sortedWith(compareAllTypeEvent())

            if (type == EventType.IN_PROGRESS) {
                var list = data.filter {
                    isInProgress(today, it.startTime, it.endTime)
                }
                //只取一条记录
                if (list.isNotEmpty()) {
                    list = list.subList(0, 1)
                }
                emit(list)
            } else {
                var list = data.filter {
                    isComingSoon(today, it.startTime)
                }
                //只取一条记录
                if (list.isNotEmpty()) {
                    list = list.subList(0, 1)
                }
                emit(list)
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getBirthdayList#type:$type")
        }
    }

    /**
     * 获取最新公会战日程
     */
    fun getClanBattleEvent(type: EventType) = flow {
        try {
            val today = getToday()
            val data = eventRepository.getClanBattleEvent(1)

            if (type == EventType.IN_PROGRESS) {
                emit(data.filter {
                    isInProgress(today, it.startTime, it.getFixedEndTime())
                })
            } else {
                emit(data.filter {
                    isComingSoon(today, it.startTime)
                })
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getClanBattleEvent#type:$type")
        }
    }
}