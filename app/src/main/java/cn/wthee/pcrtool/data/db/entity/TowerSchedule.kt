package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


/**
 * 露娜塔信息
 */
@Entity(
    tableName = "tower_schedule",
    indices = [Index(
        value = arrayOf("opening_story_id"),
        unique = false,
        name = "tower_schedule_0_opening_story_id"
    )]
)
data class TowerSchedule(
    @PrimaryKey
    @ColumnInfo(name = "tower_schedule_id") val id: Int,
    @ColumnInfo(name = "max_tower_area_id") val max_tower_area_id: Int,
    @ColumnInfo(name = "opening_story_id") val opening_story_id: Int,
    @ColumnInfo(name = "count_start_time") val count_start_time: String,
    @ColumnInfo(name = "recovery_disable_time") val recovery_disable_time: String,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
)