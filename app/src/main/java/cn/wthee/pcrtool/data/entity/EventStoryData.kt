package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 剧情活动
 */
@Entity(
    tableName = "event_story_data",
    indices = [Index(
        value = arrayOf("value"),
        unique = false,
        name = "event_story_data_0_value"
    )]
)
data class EventStoryData(
    @PrimaryKey
    @ColumnInfo(name = "story_group_id") val story_group_id: Int,
    @ColumnInfo(name = "story_type") val story_type: Int,
    @ColumnInfo(name = "value") val value: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "thumbnail_id") val thumbnail_id: Int,
    @ColumnInfo(name = "disp_order") val disp_order: Int,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
)
