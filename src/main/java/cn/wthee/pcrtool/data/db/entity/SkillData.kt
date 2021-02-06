package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 技能信息
 */
@Entity(tableName = "skill_data")
data class SkillData(
    @PrimaryKey
    @ColumnInfo(name = "skill_id") val skill_id: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "skill_type") val skill_type: Int,
    @ColumnInfo(name = "skill_area_width") val skill_area_width: Int,
    @ColumnInfo(name = "skill_cast_time") val skill_cast_time: Double,
    @ColumnInfo(name = "action_1") val action_1: Int,
    @ColumnInfo(name = "action_2") val action_2: Int,
    @ColumnInfo(name = "action_3") val action_3: Int,
    @ColumnInfo(name = "action_4") val action_4: Int,
    @ColumnInfo(name = "action_5") val action_5: Int,
    @ColumnInfo(name = "action_6") val action_6: Int,
    @ColumnInfo(name = "action_7") val action_7: Int,
    @ColumnInfo(name = "depend_action_1") val depend_action_1: Int,
    @ColumnInfo(name = "depend_action_2") val depend_action_2: Int,
    @ColumnInfo(name = "depend_action_3") val depend_action_3: Int,
    @ColumnInfo(name = "depend_action_4") val depend_action_4: Int,
    @ColumnInfo(name = "depend_action_5") val depend_action_5: Int,
    @ColumnInfo(name = "depend_action_6") val depend_action_6: Int,
    @ColumnInfo(name = "depend_action_7") val depend_action_7: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "icon_type") val icon_type: Int
) {
    fun getAllActionId() = arrayListOf(
        action_1,
        action_2,
        action_3,
        action_4,
        action_5,
        action_6,
        action_7,
        depend_action_1,
        depend_action_2,
        depend_action_3,
        depend_action_4,
        depend_action_5,
        depend_action_6,
        depend_action_7,
    ).filter {
        it != 0
    }
}