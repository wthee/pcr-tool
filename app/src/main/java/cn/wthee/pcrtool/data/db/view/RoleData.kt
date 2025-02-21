package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * 职能信息
 */
data class RoleData(
    @PrimaryKey
    @ColumnInfo(name = "unit_role_id") var roleId: Int = 0,
    @ColumnInfo(name = "unit_id") var unitId: Int = 0,
    @ColumnInfo(name = "search_area_width") var position: Int = 0,
    @ColumnInfo(name = "atk_type") var atkType: Int = 1,
)