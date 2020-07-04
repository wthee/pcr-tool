package cn.wthee.pcrtool.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment_craft")
data class EquipmentCraft(
    @PrimaryKey
    @ColumnInfo(name = "equipment_id") val equipment_id: Int,
    @ColumnInfo(name = "crafted_cost") val crafted_cost: Int,
    @ColumnInfo(name = "condition_equipment_id_1") val condition_equipment_id_1: Int,
    @ColumnInfo(name = "consume_num_1") val consume_num_1: Int,
    @ColumnInfo(name = "condition_equipment_id_2") val condition_equipment_id_2: Int,
    @ColumnInfo(name = "consume_num_2") val consume_num_2: Int,
    @ColumnInfo(name = "condition_equipment_id_3") val condition_equipment_id_3: Int,
    @ColumnInfo(name = "consume_num_3") val consume_num_3: Int,
    @ColumnInfo(name = "condition_equipment_id_4") val condition_equipment_id_4: Int,
    @ColumnInfo(name = "consume_num_4") val consume_num_4: Int,
    @ColumnInfo(name = "condition_equipment_id_5") val condition_equipment_id_5: Int,
    @ColumnInfo(name = "consume_num_5") val consume_num_5: Int,
    @ColumnInfo(name = "condition_equipment_id_6") val condition_equipment_id_6: Int,
    @ColumnInfo(name = "consume_num_6") val consume_num_6: Int,
    @ColumnInfo(name = "condition_equipment_id_7") val condition_equipment_id_7: Int,
    @ColumnInfo(name = "consume_num_7") val consume_num_7: Int,
    @ColumnInfo(name = "condition_equipment_id_8") val condition_equipment_id_8: Int,
    @ColumnInfo(name = "consume_num_8") val consume_num_8: Int,
    @ColumnInfo(name = "condition_equipment_id_9") val condition_equipment_id_9: Int,
    @ColumnInfo(name = "consume_num_9") val consume_num_9: Int,
    @ColumnInfo(name = "condition_equipment_id_10") val condition_equipment_id_10: Int,
    @ColumnInfo(name = "consume_num_10") val consume_num_10: Int
)