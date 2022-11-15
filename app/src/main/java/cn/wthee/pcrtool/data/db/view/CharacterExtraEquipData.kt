package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 * 角色可使用ex装备信息
 */
data class CharacterExtraEquipData(
    @ColumnInfo(name = "unit_id") val id: Int = 1001,
    @ColumnInfo(name = "category") var category: Int = 0,
    @ColumnInfo(name = "category_name") var categoryName: String = "",
    @ColumnInfo(name = "ex_equipment_ids") var exEquipmentIds: String = "",
)