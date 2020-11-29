package cn.wthee.pcrtool.data

import androidx.room.Dao
import androidx.room.Query
import cn.wthee.pcrtool.data.entity.EventStoryData

@Dao
interface EventDao {

    @Query("SELECT * FROM event_story_data ORDER BY story_group_id desc")
    suspend fun getAllEvents(): List<EventStoryData>
}