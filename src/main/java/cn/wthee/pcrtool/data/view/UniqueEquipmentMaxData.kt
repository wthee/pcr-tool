package cn.wthee.pcrtool.data.view

import androidx.room.ColumnInfo
import androidx.room.Embedded
import cn.wthee.pcrtool.utils.Constants.UNKNOW_EQUIP_ID
import java.io.Serializable

data class UniqueEquipmentMaxData(
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "equipment_name") val equipmentName: String,
    @ColumnInfo(name = "description") val description: String,
    @Embedded val attr: Attr,
) : Serializable {

    fun getDesc() = description.replace("\\n", "")

    companion object {
        fun unknow() =
            UniqueEquipmentMaxData(
                0,
                UNKNOW_EQUIP_ID,
                "？？？",
                "",
                Attr()
            )

    }

}