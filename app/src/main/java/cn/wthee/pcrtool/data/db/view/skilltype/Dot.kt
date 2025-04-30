package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getPercent
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 9：持续伤害
fun SkillActionDetail.dot(): String {
    tag = getString(
        when (actionDetail1) {
            0 -> R.string.skill_dot_0
            1, 7 -> R.string.skill_dot_1_7
            2 -> R.string.skill_dot_2
            3, 8 -> R.string.skill_dot_3_8
            4 -> R.string.skill_dot_4
            5 -> R.string.skill_dot_5
            11 -> R.string.skill_dot_11
            else -> R.string.unknown
        }
    )
    val value = getValueText(1, actionValue1, actionValue2, percent = getPercent())
    val time = getTimeText(3, actionValue3, actionValue4)
    val dotIncrease = if (actionDetail1 == 5) {
        getString(R.string.skill_action_dot_increase, actionValue5.toInt())
    } else {
        ""
    }
    return getString(
        R.string.skill_action_type_desc_9,
        getTarget(),
        tag,
        value,
        dotIncrease,
        time
    )
}

// 79：行动时，造成伤害
fun SkillActionDetail.actionDot(): String {
    val value = getValueText(1, actionValue1, actionValue2, percent = getPercent())
    val time = getTimeText(3, actionValue3, actionValue4)
    val type: String
    val limit: String
    if (actionDetail1 == 10) {
        type = getString(R.string.skill_hp_max)
        limit =
            getString(R.string.skill_action_damage_limit_int, actionValue5.toInt())
    } else {
        type = ""
        limit = ""
    }
    return getString(
        R.string.skill_action_type_desc_79,
        getTarget(),
        type,
        value,
        time,
        limit
    )
}