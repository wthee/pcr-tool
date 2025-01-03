package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget


// 125：无法选中
fun SkillActionDetail.cannotSelected(): String {
    val status = getString(R.string.skill_action_type_125)
    return getString(R.string.skill_action_type_desc_125, getTarget(), status)
}