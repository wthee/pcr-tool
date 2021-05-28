package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EventDao
import javax.inject.Inject

/**
 * 剧情活动 Repository
 *
 * @param eventDao
 */
class EventRepository @Inject constructor(private val eventDao: EventDao) {

    suspend fun getAllEvents() = eventDao.getAllEvents()

    suspend fun getStoryDetails(storyId: Int) = eventDao.getStoryDetails(storyId)

    suspend fun getDropEvent() = eventDao.getDropEvent()
}