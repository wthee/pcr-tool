package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EventDao

/**
 * 剧情活动 Repository
 *
 * 数据来源 [EventDao]
 */
class EventRepository(private val eventDao: EventDao) {

    suspend fun getAllEvents() = eventDao.getAllEvents()

    suspend fun getStoryDetails(storyId: Int) = eventDao.getStoryDetails(storyId)

    suspend fun getDropEvent() = eventDao.getDropEvent()

    companion object {

        fun getInstance(eventDao: EventDao) = EventRepository(eventDao)
    }
}