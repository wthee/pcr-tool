package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * 技能信息
 */
data class SkillData(
    @PrimaryKey
    @ColumnInfo(name = "skill_id") var skillId: Int = 0,
    @ColumnInfo(name = "name") var name: String? = null,
    @ColumnInfo(name = "skill_type") var skillType: Int= 0,
    @ColumnInfo(name = "skill_area_width") var skillAreaWidth: Int= 0,
    @ColumnInfo(name = "skill_cast_time") var skillCastTime: Double = 0.0,
    @ColumnInfo(name = "action_1") var action_1: Int= 0,
    @ColumnInfo(name = "action_2") var action_2: Int= 0,
    @ColumnInfo(name = "action_3") var action_3: Int= 0,
    @ColumnInfo(name = "action_4") var action_4: Int= 0,
    @ColumnInfo(name = "action_5") var action_5: Int= 0,
    @ColumnInfo(name = "action_6") var action_6: Int= 0,
    @ColumnInfo(name = "action_7") var action_7: Int= 0,
    @ColumnInfo(name = "depend_action_1") var depend_action_1: Int= 0,
    @ColumnInfo(name = "depend_action_2") var depend_action_2: Int= 0,
    @ColumnInfo(name = "depend_action_3") var depend_action_3: Int= 0,
    @ColumnInfo(name = "depend_action_4") var depend_action_4: Int= 0,
    @ColumnInfo(name = "depend_action_5") var depend_action_5: Int= 0,
    @ColumnInfo(name = "depend_action_6") var depend_action_6: Int= 0,
    @ColumnInfo(name = "depend_action_7") var depend_action_7: Int= 0,
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "icon_type") var iconType: Int= 0,
    @ColumnInfo(name = "boss_ub_cool_time") var bossUbCoolTime: Double = 0.0,
    @Ignore var rfActionIdList: List<Int> = arrayListOf(),
) {
    /**
     * 获取技能所有动作id
     */
    fun getAllActionId() = arrayListOf(
        action_1,
        action_2,
        action_3,
        action_4,
        action_5,
        action_6,
        action_7
    ).filter {
        it != 0
    }

    /**
     * 获取技能依赖信息
     */
    fun getSkillDependData(): MutableMap<Int, Int> {
        val map = mutableMapOf<Int, Int>()
        val actionList = arrayListOf(
            action_1,
            action_2,
            action_3,
            action_4,
            action_5,
            action_6,
            action_7
        )
        val dependActionList = arrayListOf(
            depend_action_1,
            depend_action_2,
            depend_action_3,
            depend_action_4,
            depend_action_5,
            depend_action_6,
            depend_action_7,
        )
        actionList.forEachIndexed { index, action ->
            map[action] = dependActionList[index]
        }
        return map
    }
}
