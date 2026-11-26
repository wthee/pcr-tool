package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import kotlin.math.abs

// 15：召唤
fun SkillActionDetail.summon(): String {
    val desc = getString(R.string.skill_action_summon_unit)
    // 召唤落点在 targetRange 里（「在自身范围 N 内 M 的位置」），
    // 必须 keepSelfRange = true，否则 targetType == 7 短路会丢掉「范围 N 内」，
    // 退化成「在自身，召唤友方单位」。
    val tgt = getTarget(keepSelfRange = true)
    return when {
        actionValue7 > 0 -> {
            getString(
                R.string.skill_action_type_desc_15,
                tgt,
                getString(R.string.skill_ahead),
                actionValue7.toInt(),
                desc
            )
        }

        actionValue7 < 0 -> {
            getString(
                R.string.skill_action_type_desc_15,
                tgt,
                getString(R.string.skill_rear),
                abs(actionValue7).toInt(),
                desc
            )
        }

        else -> {
            getString(R.string.skill_action_summon_target, tgt, desc)
        }
    }
}
