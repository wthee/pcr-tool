package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * ex冒险区域
 */
data class ExtraEquipTravelData(
    @PrimaryKey
    @ColumnInfo(name = "travel_area_id") var travelAreaId: Int = 0,
    @ColumnInfo(name = "travel_area_name") var travelAreaName: String = "",
    @ColumnInfo(name = "quest_count") var questCount: Int = 0,
    @ColumnInfo(name = "quest_ids") var questIds: String = "",
    @ColumnInfo(name = "quest_names") var questNames: String = "",
    @Ignore var questList :List<ExtraEquipQuestData> = arrayListOf()
)