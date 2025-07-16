package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 129：伤害转化
fun SkillActionDetail.damageToDot() = getString(
    R.string.skill_action_type_desc_129,
    getTarget(),
    getValueText(1, v1 = actionValue1, v2 = 0.0, percent = "%"),
    getTimeText(2, actionValue2)
)