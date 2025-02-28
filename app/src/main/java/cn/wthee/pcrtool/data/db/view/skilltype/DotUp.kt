package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getValueText

//110：持续伤害易伤
fun SkillActionDetail.dotUp(): String {
    val value = getValueText(1, actionValue1, actionValue2)
    val limit =
        getString(R.string.skill_action_damage_limit_int, actionValue7.toInt())
    return getString(R.string.skill_action_type_desc_110, getTarget(), value, limit)
}