package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import java.io.Serializable

// 剧情活动
data class EventData(
    @ColumnInfo(name = "story_id") val story_id: Int,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
    @ColumnInfo(name = "title") val title: String,
) : Serializable
