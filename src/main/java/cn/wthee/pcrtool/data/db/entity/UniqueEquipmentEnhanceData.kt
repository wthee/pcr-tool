package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

/**
 * 专武强化
 */
@Entity(
    tableName = "unique_equipment_enhance_data",
    primaryKeys = ["equip_slot", "enhance_level"]
)
class UniqueEquipmentEnhanceData(
    @ColumnInfo(name = "equip_slot") val promotionLevel: Int,
    @ColumnInfo(name = "enhance_level") val equipmentEnhanceLevel: Int,
    @ColumnInfo(name = "needed_point") val neededPoint: Int,
    @ColumnInfo(name = "total_point") val totalPoint: Int,
    @ColumnInfo(name = "needed_mana") val neededMana: Int,
    @ColumnInfo(name = "rank") val rank: Int,
) : Serializable


