package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * 技能信息
 */
data class SkillBasicData(
    @PrimaryKey
    @ColumnInfo(name = "skill_id") var skillId: Int = 0,
    @ColumnInfo(name = "name") var name: String? = null,
    @ColumnInfo(name = "skill_cast_time") var skillCastTime: Double = 0.0,
    @ColumnInfo(name = "icon_type") var iconType: Int = 0
)