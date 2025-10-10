package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getBuffText
import cn.wthee.pcrtool.utils.getPercent
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 50：持续动作
fun SkillActionDetail.channel(): String {
    val time = getTimeText(4, actionValue4, actionValue5)
    val value = getValueText(2, actionValue2, actionValue3, percent = getPercent())
    val aura = getBuffText(actionDetail1, value)
    return getString(
        R.string.skill_action_type_desc_50,
        getTarget(),
        aura,
        time,
        actionDetail3
    )
}