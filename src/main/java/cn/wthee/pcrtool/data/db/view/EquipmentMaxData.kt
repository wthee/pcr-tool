package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Embedded
import cn.wthee.pcrtool.utils.Constants.UNKNOW_EQUIP_ID
import java.io.Serializable

//装备最大强化后属性
data class EquipmentMaxData(
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "equipment_name") val equipmentName: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "craft_flg") val craftFlg: Int,
    @ColumnInfo(name = "require_level") val requireLevel: Int,
    @ColumnInfo(name = "promotion_level") val promotionLevel: Int,
    @Embedded val attr: Attr,
) : Serializable {

    fun getDesc() = description.replace("\\n", "")

    companion object {
        fun unknow() =
            EquipmentMaxData(
                UNKNOW_EQUIP_ID,
                "？？？",
                "",
                "",
                0,
                0,
                0,
                Attr()
            )

    }

}


