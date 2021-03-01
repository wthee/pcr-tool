package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 活动信息
 */
@Entity(tableName = "campaign_schedule")
data class CampaignSchedule(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "campaign_category") val type: Int,
    @ColumnInfo(name = "value") val value: Double,
    @ColumnInfo(name = "system_id") val system_id: Int,
    @ColumnInfo(name = "icon_image") val icon_image: Int,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
//    @ColumnInfo(name = "level_id") val level_id: Int,
//    @ColumnInfo(name = "shiori_group_id") val shiori_group_id: Int,
//    @ColumnInfo(name = "duplication_order") val duplication_order: Int,
)