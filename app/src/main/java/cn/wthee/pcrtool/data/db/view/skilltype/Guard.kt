package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText

// 106：守护
fun SkillActionDetail.guard(): String {
    val type = when (actionDetail1) {
        141 -> getString(R.string.skill_action_type_desc_106_type_141)
        else -> getString(R.string.skill_action_type_desc_106_type_common)
    }
    val time = getTimeText(3, actionValue3, actionValue4)
    return getString(R.string.skill_action_type_desc_106, getTarget(), type, time)
}