package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText

// 128：持续伤害增强
fun SkillActionDetail.buffDot() = getString(
    R.string.skill_action_type_desc_128,
    getTarget(),
    (actionValue1 / 100).toInt(),
    getTimeText(2, actionValue2)
)