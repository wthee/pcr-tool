package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getValueText

// 49：移除增益
fun SkillActionDetail.dispel(): String {
    val value = getValueText(1, actionValue1, actionValue2, 0.0, percent = "%")
    val type = when (actionDetail1) {
        1, 3 -> getString(R.string.skill_buff)
        2 -> getString(R.string.skill_debuff)
        10, 20 -> getString(R.string.skill_barrier)
        else -> UNKNOWN
    }
    return getString(R.string.skill_action_type_desc_49, value, getTarget(), type)
}