package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * 召唤物基本信息
 */
data class SummonData(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") var unitId: Int = 0,
    @ColumnInfo(name = "unit_name") var unitName: String = "",
    @ColumnInfo(name = "search_area_width") val position: Int = 100,
    @ColumnInfo(name = "normal_atk_cast_time") val normalAtkCastTime: Int = 100,
    @ColumnInfo(name = "atk_type") val atkType: Int = 1,
)