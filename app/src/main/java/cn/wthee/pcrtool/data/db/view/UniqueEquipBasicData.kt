package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.UNKNOWN_EQUIP_ID

/**
 * 角色专用装备信息
 */
data class UniqueEquipBasicData(
    @ColumnInfo(name = "equipment_id") val equipId: Int = UNKNOWN_EQUIP_ID,
    @ColumnInfo(name = "equipment_name") val equipName: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "unit_id") val unitId: Int = 0,
    @ColumnInfo(name = "unit_name") val unitName: String = "",
    @ColumnInfo(name = "equip_slot") val equipSlot: Int = 1,
)

/**
 * 获取专用装备标识
 */
fun getIndex(index: Int) = if (index == 1) "①" else "②"


