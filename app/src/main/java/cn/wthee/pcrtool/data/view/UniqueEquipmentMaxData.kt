package cn.wthee.pcrtool.data.view

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.io.Serializable

/**
 * 专武最大强化
 */
data class UniqueEquipmentMaxData(
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "equipment_name") val equipmentName: String,
    @ColumnInfo(name = "description") val description: String,
    @Embedded val attr: Attr,
) : Serializable {

    /**
     * 获取装备描述
     */
    fun getDesc() = description.replace("\\n", "")

}