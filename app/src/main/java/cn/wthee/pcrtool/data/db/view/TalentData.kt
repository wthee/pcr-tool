package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * 角色天赋信息
 */
data class TalentData(
    @PrimaryKey
    @ColumnInfo(name = "setting_id") val settingId: Int,
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @ColumnInfo(name = "talent_id") val talentId: Int
)