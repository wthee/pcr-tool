package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getValueText

// 90：EX被动
fun SkillActionDetail.ex(): String {
    val type = when (actionDetail1) {
        1 -> getString(R.string.attr_hp)
        2 -> getString(R.string.attr_atk)
        3 -> getString(R.string.attr_def)
        4 -> getString(R.string.attr_magic_str)
        5 -> getString(R.string.attr_magic_def)
        6 -> getString(R.string.attr_physical_critical)
        7 -> getString(R.string.attr_magic_critical)
        else -> UNKNOWN
    }
    val value = getValueText(2, actionValue2, actionValue3)

    return getString(R.string.skill_action_type_desc_90, getTarget(), type, value)
}