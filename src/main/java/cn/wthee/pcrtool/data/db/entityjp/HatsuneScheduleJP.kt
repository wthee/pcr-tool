package cn.wthee.pcrtool.data.db.entityjp

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 活动信息
 */
@Entity(tableName = "hatsune_schedule")
data class HatsuneScheduleJP(
    @PrimaryKey
    val event_id: Int,
    val teaser_time: String,
    val start_time: String,
    val end_time: String,
    val close_time: String,
    val background: Int,
    val sheet_id: String,
    val que_id: String,
    val banner_unit_id: Int,
    val count_start_time: String,
    val backgroud_size_x: Int,
    val backgroud_size_y: Int,
    val backgroud_pos_x: Int,
    val backgroud_pos_y: Int,
    val original_event_id: Int,
    //jp
//    val series_event_id: Int,
)