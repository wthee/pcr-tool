package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 16：TP 相关
fun SkillActionDetail.tp(): String {
    //tp技能限制
    if (level > Constants.TP_LIMIT_LEVEL && isRfSkill) {
        isTpLimitAction = true
    }
    val value = getValueText(1, actionValue1, actionValue2)
    tag = getString(
        when (actionDetail1) {
            1 -> R.string.skill_action_tp_recovery
            4 -> R.string.skill_action_tp_recovery_fix
            2, 3 -> R.string.skill_action_tp_reduce
            //fixme #140: actionDetail1 = 3 按比例减少
            else -> R.string.unknown
        }
    )
    return "${getTarget()}${tag} $value"
}

// 92：改变 TP 获取倍率
fun SkillActionDetail.changeTpRatio() = getString(
    R.string.skill_action_type_desc_92,
    getTarget(),
    actionValue1.toString()
)

// 97：受击tp回复
fun SkillActionDetail.tpHit(): String {
    val limit =
        getString(R.string.skill_action_limit_int, actionValue4.toInt())
    val time = getTimeText(5, actionValue5)
    val desc = getString(
        R.string.skill_action_type_desc_35,
        getTarget(),
        actionValue3.toInt(),
        time,
        limit
    )
    val tpDesc = getString(
        R.string.skill_action_type_desc_97,
        actionValue1.toInt()
    )
    return desc + tpDesc
}

// 98：改变 TP 减少时倍率
fun SkillActionDetail.tpHitReduce(): String {
    val time = getTimeText(2, actionValue2, actionValue3)
    return getString(
        R.string.skill_action_type_desc_98,
        getTarget(),
        actionValue1.toString(),
        time
    )
}