package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText

// 124：护盾（转移伤害）
fun SkillActionDetail.transferDamage(): String {
    val time = getTimeText(3, actionValue3)
    val status = getString(R.string.skill_action_type_124)
    return getString(R.string.skill_action_type_desc_116_121_123_124, getTarget(), status, time)
}