package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget


// 130：调和
fun SkillActionDetail.changeDefMax(): String {
    return getString(R.string.skill_action_type_desc_130, getTarget())
}