package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.deleteSpace
import java.io.Serializable

/**
 *  剧情活动视图
 */
data class EventData(
    @ColumnInfo(name = "event_id") val eventId: Int,
    @ColumnInfo(name = "story_id") val storyId: Int,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "unit_ids") val unitIds: String,
    @ColumnInfo(name = "unit_names") val unitNames: String,
) : Serializable {

    fun getEventTitle() = title.deleteSpace()
}
