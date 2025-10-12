package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getAtkType
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getValueText
import cn.wthee.pcrtool.utils.takeDamageTp

// 1：造成伤害
fun SkillActionDetail.damage(): String {
    val atkType = getAtkType()

    //适应伤害类型
    val adaptive = getString(
        when (actionDetail2) {
            1 -> R.string.skill_adaptive_lower_defense
            else -> R.string.none
        }
    )

    //暴伤倍率
    val multipleDamage = if (actionValue6 > 0) {
        val multiple = if (actionValue6 > 1) {
            "[${actionValue6 * 2}]"
        } else {
            "[2]"
        }
        getString(R.string.skill_critical_damage_multiple, multiple)
    } else {
        ""
    }

    //必定暴击
    val mustCritical = getString(
        if (actionValue5.toInt() == 1) {
            R.string.skill_must_critical
        } else {
            R.string.none
        }
    )

    //无视防御，fixme 需优化逻辑，龙拳为0但需要显示，106501108 106501109
    val ignoreDef =
        if (actionValue7 > 0 || actionId == 106501108 || actionId == 106501109) {
            val def = " [${actionValue7.toInt()}] "
            getString(R.string.skill_ignore_def, def)
        } else {
            ""
        }

    val value =
        getValueText(1, actionValue1, actionValue2, actionValue3, v4 = actionValue4)

    val tp = takeDamageTp()

    return getString(
        R.string.skill_action_type_desc_1,
        getTarget(),
        value,
        atkType,
        adaptive,
        multipleDamage,
        mustCritical,
        ignoreDef,
        tp
    )
}

// 46：比例伤害
fun SkillActionDetail.rateDamage(): String {
    val value = getValueText(1, actionValue1, actionValue2, percent = "%")
    val limit =
        getString(R.string.skill_action_damage_limit_int, actionValue3.toInt())
    val tp = takeDamageTp()

    return when (actionDetail1) {
        1 -> getString(R.string.skill_action_type_desc_46_1, getTarget(), value)
        2 -> getString(R.string.skill_action_type_desc_46_2, getTarget(), value)
        3 -> getString(R.string.skill_action_type_desc_46_3, getTarget(), value)
        else -> UNKNOWN
    } + (if (actionValue3 != 0.0) limit else "") + tp
}

// 34、102：伤害递增
fun SkillActionDetail.accumulativeDamage(): String {
    val value = getValueText(2, actionValue2, actionValue3)
    val limit =
        getString(R.string.skill_action_limit_int, actionValue4.toInt())
    return getString(R.string.skill_action_type_desc_34, value, limit)
}