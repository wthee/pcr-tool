package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.data.enums.DotType
import cn.wthee.pcrtool.utils.getPercent
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 9：持续伤害
fun SkillActionDetail.dot(): String {
    tag = getString(DotType.getByType(actionDetail1).typeNameId)
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