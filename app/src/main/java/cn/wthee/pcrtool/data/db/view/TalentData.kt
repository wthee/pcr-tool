package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * 角色天赋信息
 */
data class TalentData(
    @PrimaryKey
    @ColumnInfo(name = "setting_id") var settingId: Int = 0,
    @ColumnInfo(name = "unit_id") var unitId: Int = 0,
    @ColumnInfo(name = "talent_id") var talentId: Int = 0,
    @ColumnInfo(name = "search_area_width") var position: Int = 0,
    @ColumnInfo(name = "atk_type") var atkType: Int = 1,
)