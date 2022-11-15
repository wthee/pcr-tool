package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * ex冒险区域
 */
data class ExtraEquipTravelData(
    @PrimaryKey
    @ColumnInfo(name = "travel_area_id") val travelAreaId: Int,
    @ColumnInfo(name = "travel_area_name") val travelAreaName: String,
    @ColumnInfo(name = "quest_count") val questCount: Int,
    @ColumnInfo(name = "quest_ids") val questIds: String,
    @ColumnInfo(name = "quest_names") val questNames: String
)