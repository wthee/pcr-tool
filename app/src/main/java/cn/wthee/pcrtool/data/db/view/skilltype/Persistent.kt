package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText

//116：执着状态
fun SkillActionDetail.persistent(): String {
    val time = getTimeText(1, actionValue1)
    val status = getString(R.string.skill_action_type_116)
    return getString(R.string.skill_action_type_desc_116_121_123, getTarget(), status, time)
}