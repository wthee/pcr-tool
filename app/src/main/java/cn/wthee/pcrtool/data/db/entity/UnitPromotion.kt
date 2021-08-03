package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

/**
 * 角色 Rank 装备信息
 */
@Entity(
    tableName = "unit_promotion",
    indices = [Index(
        value = arrayOf("unit_id"),
        unique = false,
        name = "unit_promotion_0_unit_id"
    )],
    primaryKeys = ["unit_id", "promotion_level"]
)
data class UnitPromotion(
    @ColumnInfo(name = "unit_id") val unitId: Int = 100101,
    @ColumnInfo(name = "promotion_level") val promotionLevel: Int = 10,
    @ColumnInfo(name = "equip_slot_1") val equipSlot1: Int = 0,
    @ColumnInfo(name = "equip_slot_2") val equipSlot2: Int = 0,
    @ColumnInfo(name = "equip_slot_3") val equipSlot3: Int = 0,
    @ColumnInfo(name = "equip_slot_4") val equipSlot4: Int = 0,
    @ColumnInfo(name = "equip_slot_5") val equipSlot5: Int = 0,
    @ColumnInfo(name = "equip_slot_6") val equipSlot6: Int = 0
) {

    fun getAllOrderIds(): ArrayList<Int> {
        val ids = arrayListOf<Int>()
        ids.add(equipSlot1)
        ids.add(equipSlot2)
        ids.add(equipSlot3)
        ids.add(equipSlot4)
        ids.add(equipSlot5)
        ids.add(equipSlot6)
        return ids
    }

    fun getRowIds(): ArrayList<Int> {
        val ids = arrayListOf<Int>()
        ids.add(equipSlot1)
        ids.add(equipSlot3)
        ids.add(equipSlot5)
        ids.add(equipSlot2)
        ids.add(equipSlot4)
        ids.add(equipSlot6)
        return ids
    }
}





