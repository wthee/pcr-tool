package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTimeText

// 22：循环变更
fun SkillActionDetail.changePattern() = when (actionDetail1) {
    1 -> getString(
        R.string.skill_action_loop_change,
        if (actionValue1 > 0) {
            getTimeText(1, actionValue1)
        } else {
            ""
        }
    )

    2 -> getString(
        R.string.skill_action_skill_anim_change,
        getTimeText(1, actionValue1)
    )

    else -> UNKNOWN
}
