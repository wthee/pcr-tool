package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTimeText

// 14：行动模式变更
fun SkillActionDetail.changeMode() = when (actionDetail1) {
    1 -> {
        val status = when (actionValue5.toInt()) {
            1 -> getString(R.string.skill_action_loop_change_flight_status)
            else -> getString(R.string.none)
        }
        getString(
            R.string.skill_action_loop_change,
            status,
            getTimeText(1, actionValue1)
        )
    }
    2 -> getString(R.string.skill_action_type_desc_14_2, actionValue1.toInt())
    3 -> getString(R.string.skill_action_type_desc_14_3)
    else -> UNKNOWN
}