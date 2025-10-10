package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.data.enums.BuffType
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getValueText

// 90：EX被动
fun SkillActionDetail.ex(): String {
    val type = getString(BuffType.getByValue(actionDetail1).nameId)
    val value = getValueText(2, actionValue2, actionValue3)

    return getString(R.string.skill_action_type_desc_90, getTarget(), type, value)
}