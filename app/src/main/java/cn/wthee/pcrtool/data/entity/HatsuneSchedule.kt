package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 活动信息
 */
@Entity(tableName = "hatsune_schedule")
data class HatsuneSchedule(
    @PrimaryKey
    @ColumnInfo(name = "event_id") val event_id: Int,
    @ColumnInfo(name = "teaser_time") val teaser_time: String,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
    @ColumnInfo(name = "close_time") val close_time: String,
    @ColumnInfo(name = "background") val background: Int,
    @ColumnInfo(name = "sheet_id") val sheet_id: String,
    @ColumnInfo(name = "que_id") val que_id: String,
    @ColumnInfo(name = "banner_unit_id") val banner_unit_id: Int,
    @ColumnInfo(name = "count_start_time") val count_start_time: String,
    @ColumnInfo(name = "backgroud_size_x") val backgroud_size_x: Int,
    @ColumnInfo(name = "backgroud_size_y") val backgroud_size_y: Int,
    @ColumnInfo(name = "backgroud_pos_x") val backgroud_pos_x: Int,
    @ColumnInfo(name = "backgroud_pos_y") val backgroud_pos_y: Int,
    @ColumnInfo(name = "original_event_id") val original_event_id: Int,
//    @ColumnInfo(name = "series_event_id") val series_event_id: Int,
)