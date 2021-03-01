package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.data.view.Attr

/**
 * 装备强化提升属性
 */
@Entity(tableName = "equipment_enhance_rate")
data class EquipmentEnhanceRate(
    @PrimaryKey
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "equipment_name") val equipmentName: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "promotion_level") val promotionLevel: Int,
    @Embedded val attr: Attr,
)