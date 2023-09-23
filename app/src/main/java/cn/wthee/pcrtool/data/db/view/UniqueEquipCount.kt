package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 * 角色专用装备数量
 */
data class UniqueEquipCount(
    @ColumnInfo(name = "equip_slot") val slot: Int = 0,
    @ColumnInfo(name = "count") val count: Int = 0
)



