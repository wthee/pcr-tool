package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EventDao
import javax.inject.Inject

/**
 * 剧情活动 Repository
 *
 * @param eventDao
 */
class EventRepository @Inject constructor(private val eventDao: EventDao) {

    suspend fun getAllEvents(limit: Int) = eventDao.getAllEvents(limit)

    suspend fun getStoryDetails(storyId: Int) = eventDao.getStoryDetails(storyId)

    suspend fun getDropEvent(limit: Int) = eventDao.getDropEvent(limit)

    suspend fun getMissionEvent(limit: Int) = eventDao.getMissionEvent(limit)

    suspend fun getLoginEvent(limit: Int) = eventDao.getLoginEvent(limit)

    suspend fun getFortuneEvent(limit: Int) = eventDao.getFortuneEvent(limit)

    suspend fun getTowerEvent(limit: Int) = eventDao.getTowerEvent(limit)

    suspend fun getSpDungeonEvent(limit: Int) = try {
        eventDao.getSpDungeonEvent(limit)
    } catch (_: Exception) {
        arrayListOf()
    }

    suspend fun getFaultEvent(limit: Int) = try {
        eventDao.getFaultEvent(limit)
    } catch (_: Exception) {
        arrayListOf()
    }

    suspend fun getFreeGachaEvent(limit: Int) = eventDao.getFreeGachaEvent(limit)

    suspend fun getBirthdayList() = eventDao.getBirthdayList()

    suspend fun getClanBattleEvent(limit: Int) = eventDao.getClanBattleEvent(limit)

    suspend fun getColosseumEvent(limit: Int) = try {
        eventDao.getColosseumEvent(limit)
    } catch (_: Exception) {
        arrayListOf()
    }
}