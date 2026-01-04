package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import kotlin.math.abs


// 35：特殊标记
fun SkillActionDetail.seal(): String {
    val count = abs(actionValue4.toInt())
    return if (actionValue4.toInt() > 0) {
        val time = getTimeText(3, actionValue3, hideIndex = true)
        val limit =
            getString(R.string.skill_action_limit_int, actionValue1.toInt())
        getString(R.string.skill_action_type_desc_35, getTarget(), count, time, limit)
    } else {
        getString(
            R.string.skill_action_type_desc_35_reduce,
            getTarget(),
            count
        )
    }
}

// 60：标记赋予
fun SkillActionDetail.attackSeal(): String {
    val limit =
        getString(R.string.skill_action_limit_int, actionValue1.toInt())
    val time = getTimeText(3, actionValue3, actionValue4)
    val target = getTarget()
    val desc = getString(R.string.skill_action_type_desc_60_0, time, limit)


    return if (actionDetail1 == 3) {
        getString(R.string.skill_action_type_desc_60_1, target, desc)
    } else if (actionDetail1 == 1 && actionDetail3 == 1) {
        getString(R.string.skill_action_type_desc_60_2, target, desc)
    } else if (actionDetail1 == 4 && actionDetail3 == 1) {
        getString(R.string.skill_action_type_desc_60_3, target, desc)
    } else if (actionDetail1 == 5 && actionDetail3 == 1) {
        getString(R.string.skill_action_type_desc_60_4, target, desc)
    } else {
        UNKNOWN
    }
}

// 77：被动叠加标记
fun SkillActionDetail.ifBuffSeal(): String {
    val time = getTimeText(3, actionValue3, actionValue4)
    val effect = when (actionDetail1) {
        1 -> getString(R.string.skill_action_type_desc_77_1) + getString(R.string.skill_buff)
        2 -> getString(R.string.skill_action_type_desc_77_1) + getString(R.string.skill_damage)
        3 -> getString(R.string.skill_action_type_desc_77_1) + getString(R.string.skill_status_down)
        4 -> getString(R.string.skill_status_ub)
        else -> UNKNOWN
    }
    val limit =
        getString(R.string.skill_action_limit_int, actionValue1.toInt())

    return getString(
        R.string.skill_action_type_desc_77,
        if (actionDetail1 != 4) getTarget() else "",
        effect,
        actionDetail2,
        time,
        limit
    )
}

// 101：特殊标记v2
fun SkillActionDetail.sealV2(): String {
    val count = abs(actionDetail2)
    return if (actionDetail2 >= 0) {
        val time = getTimeText(3, actionValue3, hideIndex = true)
        val limit = getString(R.string.skill_action_limit_int, actionValue1.toInt())
        getString(R.string.skill_action_type_desc_101, getTarget(), count, time, limit)
    } else {
        getString(
            R.string.skill_action_type_desc_101_reduce,
            getTarget(),
            count
        )
    }
}

// 114：特殊标记计数？
fun SkillActionDetail.sealCount(): String {
    val action1 = actionDetail1 % 100
    val action2 = actionDetail2 % 100
    val time = getTimeText(4, actionValue4, hideIndex = true)
    val aura = getString(R.string.skill_action_type_desc_114_aura, actionValue3.toInt())

    return getString(
        R.string.skill_action_type_desc_114,
        getTarget(),
        actionValue2.toInt(),
        aura,
        action1,
        action2,
        time
    )
}


// 133：标记消耗
fun SkillActionDetail.sealConsume(): String {
    val action1 = actionDetail1 % 100
    val time = getTimeText(4, actionValue4, hideIndex = true)
    // 使用 UB 时？
    return getString(
        R.string.skill_action_type_desc_133,
        getTarget(),
        actionValue2.toInt(),
        action1,
        time
    )
}