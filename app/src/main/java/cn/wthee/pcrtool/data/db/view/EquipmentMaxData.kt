package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Embedded
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID

/**
 * 装备最大强化后属性视图
 */
data class EquipmentMaxData(
    @ColumnInfo(name = "equipment_id") var equipmentId: Int = UNKNOWN_EQUIP_ID,
    @ColumnInfo(name = "equipment_name") var equipmentName: String = "",
    @ColumnInfo(name = "type") var type: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "craft_flg") var craftFlg: Int = 0,
    @ColumnInfo(name = "require_level") var requireLevel: Int = 0,
    @ColumnInfo(name = "promotion_level") var promotionLevel: Int = 0,
    @Embedded var attr: Attr = Attr(),
) {

    /**
     * 装备描述
     */
    fun getDesc() = description.replace("\\n", "")

    companion object {
        fun unknown() =
            EquipmentMaxData(
                UNKNOWN_EQUIP_ID,
                "?",
                "",
                "",
                0,
                0,
                0,
                Attr()
            )

    }

}

