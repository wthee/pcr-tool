package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.deleteSpace

/**
 *  剧情活动视图
 */
data class EventData(
    @ColumnInfo(name = "event_id") val eventId: Int = 10001,
    @ColumnInfo(name = "story_id") val storyId: Int = 1001,
    @ColumnInfo(name = "start_time") val startTime: String = "2020/01/01 00:00:00",
    @ColumnInfo(name = "end_time") val endTime: String = "2020/01/07 00:00:00",
    @ColumnInfo(name = "title") val title: String = "???",
    @ColumnInfo(name = "unit_ids") val unitIds: String = "100101",
    @ColumnInfo(name = "unit_names") val unitNames: String = "?",
) {

    fun getEventTitle() = title.deleteSpace
}
