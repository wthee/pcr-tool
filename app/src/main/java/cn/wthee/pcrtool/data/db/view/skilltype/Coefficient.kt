package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.data.enums.SkillActionType
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getValueText

// 26：系数提升
fun SkillActionDetail.coefficient(): String {
    val attrType = when (actionValue1.toInt()) {
        7 -> getString(R.string.skill_physical_str)
        8 -> getString(R.string.skill_magic_str)
        9 -> getString(R.string.skill_physical_def)
        10 -> getString(R.string.skill_magic_def)
        else -> UNKNOWN
    }
    val changeType = when (SkillActionType.getByType(actionType)) {
        SkillActionType.ADDITIVE -> getString(R.string.skill_action_type_desc_additive)
        SkillActionType.MULTIPLE -> getString(R.string.skill_action_type_desc_multiple)
        SkillActionType.DIVIDE -> getString(R.string.skill_action_type_desc_divide)
        else -> UNKNOWN
    }
    val value = getValueText(2, actionValue2, actionValue3, hideIndex = true)
    val commonDesc = getString(
        R.string.skill_action_change_coe,
        actionDetail1 % 100,
        actionDetail2,
        changeType,
        value
    )

    val extraDesc = when (actionValue1.toInt()) {
        2 -> {
            val mValue = when {
                actionDetail3 == 0 -> {
                    "[${actionValue2}]"
                }

                actionDetail2 == 0 -> {
                    "[${actionValue3}]"
                }

                else -> {
                    "[${(actionValue2 + 2 * actionValue3 * level)}] <$actionValue2 + ${2 * actionValue3} * 技能等级> "
                }
            }
            val mDesc = getString(
                R.string.skill_action_change_coe,
                actionDetail1 % 100,
                actionDetail2,
                changeType,
                mValue
            )
            getString(R.string.skill_action_change_coe_2, mDesc)
        }

        0 -> getString(R.string.skill_action_change_coe_0, commonDesc)
        1 -> getString(R.string.skill_action_change_coe_1, commonDesc)
        4 -> {
            val targetStr = getTarget()
            val targetType = if (targetStr != "") {
                targetStr
            } else {
                getString(R.string.skill_target_none)
            }
            getString(R.string.skill_action_change_coe_4, commonDesc, targetType)

        }

        5 -> getString(R.string.skill_action_change_coe_5, commonDesc)
        6 -> getString(R.string.skill_action_change_coe_6, commonDesc)
        in 7..10 -> getString(
            R.string.skill_action_change_coe_7_10,
            commonDesc,
            getTarget(),
            attrType
        )

        12 -> getString(R.string.skill_action_change_coe_12, commonDesc, getTarget())
        13 -> getString(R.string.skill_action_change_coe_13, commonDesc)
        15 -> getString(R.string.skill_action_change_coe_15, commonDesc)
        16 -> getString(R.string.skill_action_change_coe_16, commonDesc)
        102 -> getString(R.string.skill_action_change_coe_102, commonDesc)
        in 20 until 30 -> getString(
            R.string.skill_action_change_coe_skill_count,
            commonDesc
        )

        in 200 until 300,
        in 2112 until 3000 -> getString(
            R.string.skill_action_change_coe_mark_count,
            commonDesc
        )

        else -> UNKNOWN
    }

    //上限判断
    return if (actionValue4.toInt() != 0) {
        val limitValue = getValueText(4, actionValue4, actionValue5, hideIndex = true)
        val limit = getString(R.string.skill_action_limit, limitValue)
        extraDesc + limit
    } else {
        extraDesc
    }
}