package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getBuffText
import cn.wthee.pcrtool.utils.getPercent
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText
import cn.wthee.pcrtool.utils.initOtherLimit

// 10：buff/debuff
fun SkillActionDetail.aura(): String {
    tag = getString(
        if (actionDetail1 % 10 == 0) {
            R.string.skill_buff
        } else {
            R.string.skill_debuff
        }
    )
    if (actionDetail1 % 1000 / 10 == 5) {
        //回避等技能限制
        initOtherLimit()
    }
    val value = getValueText(2, actionValue2, actionValue3, percent = getPercent())
    val aura = getBuffText(actionDetail1, value, actionValue7)
    val time = getTimeText(4, actionValue4, actionValue5)

    return if (actionDetail2 == 2) {
        getString(R.string.skill_action_type_desc_10_break, getTarget(), aura)
    } else {
        "${getTarget()}${aura}$time"
    }
}