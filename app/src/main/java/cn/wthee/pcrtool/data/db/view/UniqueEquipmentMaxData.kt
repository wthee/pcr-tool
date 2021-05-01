package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Embedded
import cn.wthee.pcrtool.utils.Constants
import java.io.Serializable

/**
 * 专武最大强化
 */
data class UniqueEquipmentMaxData(
    @ColumnInfo(name = "unit_id") val unitId: Int = 0,
    @ColumnInfo(name = "equipment_id") val equipmentId: Int = Constants.UNKNOWN_EQUIP_ID,
    @ColumnInfo(name = "equipment_name") val equipmentName: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @Embedded val attr: Attr = Attr(),
) : Serializable {

    /**
     * 获取装备描述
     */
    fun getDesc() = description.replace("\\n", "")

}