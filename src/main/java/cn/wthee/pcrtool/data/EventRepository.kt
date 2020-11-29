package cn.wthee.pcrtool.data


//怪物数据Repository
class EventRepository(private val eventDao: EventDao) {

    //获取活动事件信息
    suspend fun getAllEvents() = eventDao.getAllEvents()

    companion object {

        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(eventDao: EventDao) =
            instance ?: synchronized(this) {
                instance ?: EventRepository(eventDao).also { instance = it }
            }
    }
}