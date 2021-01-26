package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 活动信息
 */
data class DropEvent(
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "multiple") val multiple: Double,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
)