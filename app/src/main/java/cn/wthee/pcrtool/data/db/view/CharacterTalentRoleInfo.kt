package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 * 角色天赋、职能信息
 */
data class CharacterTalentRoleInfo(
    @ColumnInfo(name = "unit_id") var unitId: Int = 0,
    @ColumnInfo(name = "talent_id") var talentId: Int = 0,
    @ColumnInfo(name = "unit_role_id") var roleId: Int = 0,
    @ColumnInfo(name = "search_area_width") var position: Int = 0,
    @ColumnInfo(name = "atk_type") var atkType: Int = 1,

    )