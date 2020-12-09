package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.data.db.view.EquipmentMaterial

//装备合成信息
@Entity(tableName = "equipment_craft")
data class EquipmentCraft(
    @PrimaryKey
    @ColumnInfo(name = "equipment_id") val equipment_id: Int,
    @ColumnInfo(name = "crafted_cost") val crafted_cost: Int,
    @ColumnInfo(name = "condition_equipment_id_1") val cid1: Int,
    @ColumnInfo(name = "consume_num_1") val count1: Int,
    @ColumnInfo(name = "condition_equipment_id_2") val cid2: Int,
    @ColumnInfo(name = "consume_num_2") val count2: Int,
    @ColumnInfo(name = "condition_equipment_id_3") val cid3: Int,
    @ColumnInfo(name = "consume_num_3") val count3: Int,
    @ColumnInfo(name = "condition_equipment_id_4") val cid4: Int,
    @ColumnInfo(name = "consume_num_4") val count4: Int,
    @ColumnInfo(name = "condition_equipment_id_5") val cid5: Int,
    @ColumnInfo(name = "consume_num_5") val count5: Int,
    @ColumnInfo(name = "condition_equipment_id_6") val cid6: Int,
    @ColumnInfo(name = "consume_num_6") val count6: Int,
    @ColumnInfo(name = "condition_equipment_id_7") val cid7: Int,
    @ColumnInfo(name = "consume_num_7") val count7: Int,
    @ColumnInfo(name = "condition_equipment_id_8") val cid8: Int,
    @ColumnInfo(name = "consume_num_8") val count8: Int,
    @ColumnInfo(name = "condition_equipment_id_9") val cid9: Int,
    @ColumnInfo(name = "consume_num_9") val count9: Int,
    @ColumnInfo(name = "condition_equipment_id_10") val cid10: Int,
    @ColumnInfo(name = "consume_num_10") val count10: Int
) {
    fun getAllMaterialId(): ArrayList<EquipmentMaterial> {
        val list = arrayListOf<EquipmentMaterial>()
        if (cid1 != 0 && count1 != 0) list.add(
            EquipmentMaterial(
                cid1,
                "",
                count1
            )
        )
        if (cid2 != 0 && count2 != 0) list.add(
            EquipmentMaterial(
                cid2,
                "",
                count2
            )
        )
        if (cid3 != 0 && count3 != 0) list.add(
            EquipmentMaterial(
                cid3,
                "",
                count3
            )
        )
        if (cid4 != 0 && count4 != 0) list.add(
            EquipmentMaterial(
                cid4,
                "",
                count4
            )
        )
        if (cid5 != 0 && count5 != 0) list.add(
            EquipmentMaterial(
                cid5,
                "",
                count5
            )
        )
        if (cid6 != 0 && count6 != 0) list.add(
            EquipmentMaterial(
                cid6,
                "",
                count6
            )
        )
        if (cid7 != 0 && count7 != 0) list.add(
            EquipmentMaterial(
                cid7,
                "",
                count7
            )
        )
        if (cid8 != 0 && count8 != 0) list.add(
            EquipmentMaterial(
                cid8,
                "",
                count8
            )
        )
        if (cid9 != 0 && count9 != 0) list.add(
            EquipmentMaterial(
                cid9,
                "",
                count9
            )
        )
        if (cid10 != 0 && count10 != 0) list.add(
            EquipmentMaterial(
                cid10,
                "",
                count10
            )
        )
        return list
    }
}