package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.PrimaryKey

/**
 * 装备强化提升属性
 */
data class EquipmentEnhanceRate(
    @PrimaryKey
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "equipment_name") val equipmentName: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "promotion_level") val promotionLevel: Int,
    @Embedded val attr: Attr,
)