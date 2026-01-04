package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getEffectType
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 78：被击伤害上升
fun SkillActionDetail.damageTakenUp(): String {
    val time = getTimeText(3, actionValue3, actionValue4)
    val limit = getString(R.string.skill_action_limit_int, actionValue2.toInt())
    //数量类型
    val countType = when (actionDetail1) {
        1 -> getString(R.string.skill_action_type_desc_78_1)
        else -> getString(R.string.unknown)
    }
    //增加或减少
    val effectType = getEffectType(actionDetail2)
    //倍数计算公式
    val valueText = "<${actionValue1} * ${countType}>"
    return getString(
        R.string.skill_action_type_desc_78,
        getTarget(),
        effectType,
        valueText,
        time,
        limit
    )
}

// 72：伤害减免
fun SkillActionDetail.damageReduce(): String {
    val type = when (actionDetail1) {
        1 -> getString(R.string.skill_physical)
        2 -> getString(R.string.skill_magic)
        3 -> getString(R.string.skill_all)
        else -> UNKNOWN
    }
    val value =
        getValueText(1, actionValue1, actionValue2, percent = "%")
    val time = getTimeText(3, actionValue3, actionValue4)
    return getString(R.string.skill_action_type_desc_72, getTarget(), type, value, time)
}