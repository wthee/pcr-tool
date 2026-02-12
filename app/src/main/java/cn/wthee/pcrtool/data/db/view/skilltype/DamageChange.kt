package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getEffectType
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 132：伤害变更
fun SkillActionDetail.damageChange(): String {
    //数值
    val value =
        getValueText(1, v1 = actionValue1, v2 = actionValue2, percent = "%")
    //限制
    val limit =
        getString(R.string.skill_action_damage_limit_int, actionValue3.toInt())
    //增加或减少
    val effectType = getEffectType(actionDetail1)
    //持续时间
    val time = getTimeText(4, actionValue4)

    return getString(
        R.string.skill_action_type_desc_132,
        getTarget(),
        effectType,
        value,
        time,
        limit,
    )
}
