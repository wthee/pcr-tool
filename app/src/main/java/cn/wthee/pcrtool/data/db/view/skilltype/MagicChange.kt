package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 121：幻化状态
fun SkillActionDetail.magicChange(): String {
    val time = getTimeText(5, actionValue5)
    val status = getString(R.string.skill_action_type_121)
    return getString(R.string.skill_action_type_desc_116_121_123, getTarget(), status, time)
}

// 123：减伤状态
fun SkillActionDetail.magicChangeReduceDamage(): String {
    val time = getTimeText(3, actionValue3)
    val value = getValueText(1, v1 = actionValue1, v2 = actionValue2, percent = "%")
    val desc = getString(R.string.skill_action_type_desc_123_1, value)
    return getString(R.string.skill_action_type_desc_116_121_123, getTarget(), desc, time)
}