package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 剧情活动（包含复刻）
 */
@Entity(
    tableName = "event_top_adv",
    indices = [Index(
        value = arrayOf("event_id", "type"),
        unique = false,
        name = "event_top_adv_0_event_id_1_type"
    )]
)
data class EventTopAdv(
    @PrimaryKey
    @ColumnInfo(name = "event_top_adv_id") val event_top_adv_id: Int,
    @ColumnInfo(name = "event_id") val event_id: Int,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "value_1") val value_1: Int,
    @ColumnInfo(name = "value_2") val value_2: Int,
    @ColumnInfo(name = "value_3") val value_3: Int,
    @ColumnInfo(name = "story_id") val story_id: Int,
    @ColumnInfo(name = "character_id") val character_id: Int,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
)
