package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTimeText

// 105：环境效果
fun SkillActionDetail.environment(): String {
    val type = when (actionDetail2) {
        137 -> getString(R.string.skill_status_3137)
        162 -> getString(R.string.skill_status_3162)
        175 -> getString(R.string.skill_status_3175)
        else -> getString(R.string.unknown)
    }
    val time = getTimeText(1, actionValue1)
    return getString(R.string.skill_action_type_desc_105, type, time)
}