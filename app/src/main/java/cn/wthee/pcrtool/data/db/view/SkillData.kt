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
    @ColumnInfo(name = "skill_type") var skillType: Int = 0,
    @ColumnInfo(name = "skill_area_width") var skillAreaWidth: Int = 0,
    @ColumnInfo(name = "skill_cast_time") var skillCastTime: Double = 0.0,
    @ColumnInfo(name = "action_1") var action_1: Int = 0,
    @ColumnInfo(name = "action_2") var action_2: Int = 0,
    @ColumnInfo(name = "action_3") var action_3: Int = 0,
    @ColumnInfo(name = "action_4") var action_4: Int = 0,
    @ColumnInfo(name = "action_5") var action_5: Int = 0,
    @ColumnInfo(name = "action_6") var action_6: Int = 0,
    @ColumnInfo(name = "action_7") var action_7: Int = 0,
    @ColumnInfo(name = "action_8") var action_8: Int = 0,
    @ColumnInfo(name = "action_9") var action_9: Int = 0,
    @ColumnInfo(name = "action_10") var action_10: Int = 0,
    @ColumnInfo(name = "action_11") var action_11: Int = 0,
    @ColumnInfo(name = "action_12") var action_12: Int = 0,
    @ColumnInfo(name = "action_13") var action_13: Int = 0,
    @ColumnInfo(name = "action_14") var action_14: Int = 0,
    @ColumnInfo(name = "action_15") var action_15: Int = 0,
    @ColumnInfo(name = "action_16") var action_16: Int = 0,
    @ColumnInfo(name = "action_17") var action_17: Int = 0,
    @ColumnInfo(name = "action_18") var action_18: Int = 0,
    @ColumnInfo(name = "action_19") var action_19: Int = 0,
    @ColumnInfo(name = "action_20") var action_20: Int = 0,
    @ColumnInfo(name = "depend_action_1") var depend_action_1: Int = 0,
    @ColumnInfo(name = "depend_action_2") var depend_action_2: Int = 0,
    @ColumnInfo(name = "depend_action_3") var depend_action_3: Int = 0,
    @ColumnInfo(name = "depend_action_4") var depend_action_4: Int = 0,
    @ColumnInfo(name = "depend_action_5") var depend_action_5: Int = 0,
    @ColumnInfo(name = "depend_action_6") var depend_action_6: Int = 0,
    @ColumnInfo(name = "depend_action_7") var depend_action_7: Int = 0,
    @ColumnInfo(name = "depend_action_8") var depend_action_8: Int = 0,
    @ColumnInfo(name = "depend_action_9") var depend_action_9: Int = 0,
    @ColumnInfo(name = "depend_action_10") var depend_action_10: Int = 0,
    @ColumnInfo(name = "depend_action_11") var depend_action_11: Int = 0,
    @ColumnInfo(name = "depend_action_12") var depend_action_12: Int = 0,
    @ColumnInfo(name = "depend_action_13") var depend_action_13: Int = 0,
    @ColumnInfo(name = "depend_action_14") var depend_action_14: Int = 0,
    @ColumnInfo(name = "depend_action_15") var depend_action_15: Int = 0,
    @ColumnInfo(name = "depend_action_16") var depend_action_16: Int = 0,
    @ColumnInfo(name = "depend_action_17") var depend_action_17: Int = 0,
    @ColumnInfo(name = "depend_action_18") var depend_action_18: Int = 0,
    @ColumnInfo(name = "depend_action_19") var depend_action_19: Int = 0,
    @ColumnInfo(name = "depend_action_20") var depend_action_20: Int = 0,
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "icon_type") var iconType: Int = 0,
    @ColumnInfo(name = "boss_ub_cool_time") var bossUbCoolTime: Double = 0.0,
    @Ignore var isRfSkill: Boolean = false,
    @Ignore var isOtherRfSkill: Boolean = false,
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
        action_7,
        action_8,
        action_9,
        action_10,
        action_11,
        action_12,
        action_13,
        action_14,
        action_15,
        action_16,
        action_17,
        action_18,
        action_19,
        action_20,
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
            action_7,
            action_8,
            action_9,
            action_10,
            action_11,
            action_12,
            action_13,
            action_14,
            action_15,
            action_16,
            action_17,
            action_18,
            action_19,
            action_20,
        )
        val dependActionList = arrayListOf(
            depend_action_1,
            depend_action_2,
            depend_action_3,
            depend_action_4,
            depend_action_5,
            depend_action_6,
            depend_action_7,
            depend_action_8,
            depend_action_9,
            depend_action_10,
            depend_action_11,
            depend_action_12,
            depend_action_13,
            depend_action_14,
            depend_action_15,
            depend_action_16,
            depend_action_17,
            depend_action_18,
            depend_action_19,
            depend_action_20,
        )
        actionList.forEachIndexed { index, action ->
            map[action] = dependActionList[index]
        }
        return map
    }
}
