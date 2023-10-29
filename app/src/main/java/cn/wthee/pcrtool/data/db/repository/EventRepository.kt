package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EventDao
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.compareAllTypeEvent
import javax.inject.Inject

/**
 * 剧情活动 Repository
 *
 * @param eventDao
 */
class EventRepository @Inject constructor(private val eventDao: EventDao) {

    suspend fun getAllEvents(limit: Int) = try {
        eventDao.getAllEvents(limit)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getAllEvents")
        arrayListOf()
    }

    suspend fun getStoryDetails(storyId: Int) = eventDao.getStoryDetails(storyId)

    suspend fun getDropEvent(limit: Int) = try {
        eventDao.getDropEvent(limit)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getDropEvent")
        arrayListOf()
    }

    suspend fun getMissionEvent(limit: Int) = try {
        eventDao.getMissionEvent(limit)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getMissionEvent")
        arrayListOf()
    }

    suspend fun getLoginEvent(limit: Int) = try {
        eventDao.getLoginEvent(limit)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getLoginEvent")
        arrayListOf()
    }

    suspend fun getFortuneEvent(limit: Int) = try {
        eventDao.getFortuneEvent(limit)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getFortuneEvent")
        arrayListOf()
    }

    suspend fun getTowerEvent(limit: Int) = try {
        eventDao.getTowerEvent(limit)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getTowerEvent")
        arrayListOf()
    }

    suspend fun getSpDungeonEvent(limit: Int) = try {
        eventDao.getSpDungeonEvent(limit)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getSpDungeonEvent")
        arrayListOf()
    }

    suspend fun getFaultEvent(limit: Int) = try {
        eventDao.getFaultEvent(limit)
    } catch (_: Exception) {
        arrayListOf()
    }

    suspend fun getFreeGachaEvent(limit: Int) = try {
        eventDao.getFreeGachaEvent(limit)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getFreeGachaList#type")
        emptyList()
    }

    suspend fun getBirthdayList() = try {
        eventDao.getBirthdayList().sortedWith(compareAllTypeEvent())
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getBirthdayList#type")
        emptyList()
    }

    suspend fun getClanBattleEvent(limit: Int) = try {
        eventDao.getClanBattleEvent(limit)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getClanBattleEvent")
        emptyList()
    }

    suspend fun getColosseumEvent(limit: Int) = try {
        eventDao.getColosseumEvent(limit)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getColosseumEvent")
        emptyList()
    }
}