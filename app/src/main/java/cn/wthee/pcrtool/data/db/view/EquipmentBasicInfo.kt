package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID

/**
 * 装备基础信息
 */
data class EquipmentBasicInfo(
    @ColumnInfo(name = "equipment_id") var equipmentId: Int = UNKNOWN_EQUIP_ID,
    @ColumnInfo(name = "equipment_name") var equipmentName: String = "",
    @ColumnInfo(name = "craft_flg") var craftFlg: Int = 0,
    @ColumnInfo(name = "promotion_level") var promotionLevel: Int = 0,
    @ColumnInfo(name = "require_level") var requireLevel: Int = 0,
)

