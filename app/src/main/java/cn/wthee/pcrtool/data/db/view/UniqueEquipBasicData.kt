package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 * 角色专用装备信息
 */
data class UniqueEquipBasicData(
    @ColumnInfo(name = "equipment_id") val equipId: Int = -1,
    @ColumnInfo(name = "equipment_name") val equipName: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "unit_id") val unitId: Int = 0,
    @ColumnInfo(name = "unit_name") val unitName: String = "",
)





