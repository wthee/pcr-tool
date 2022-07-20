package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * 角色特殊技能标签
 */
data class SpSkillLabelData(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @ColumnInfo(name = "normal_label_text") val normalLabelText: String,
    @ColumnInfo(name = "sp_label_text") val spLabelText: String
)