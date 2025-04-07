package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 17：触发条件
fun SkillActionDetail.trigger(): String {
    val desc = when (actionDetail1) {
        2 -> getString(R.string.skill_action_type_desc_17_2, actionValue1.toInt())
        3 -> getString(R.string.skill_action_type_desc_17_3, actionValue3.toInt())
        4 -> getString(R.string.skill_action_type_desc_17_4, actionValue1.toInt())
        5 -> getString(R.string.skill_action_type_desc_17_5, actionValue1.toInt())
        7 -> getString(R.string.skill_action_type_desc_17_7, actionValue3.toInt())
        8 -> getString(R.string.skill_action_type_desc_17_8, actionValue1.toInt())
        9 -> getString(
            R.string.skill_action_type_desc_17_9,
            actionValue1.toInt(),
            getTimeText(3, actionValue3)
        )

        10 -> getString(R.string.skill_action_type_desc_17_10, actionValue1.toInt())
        11 -> getString(R.string.skill_action_type_desc_17_11)
        13 -> getString(R.string.skill_action_type_desc_17_13, actionValue3.toInt())
        14 -> getString(R.string.skill_action_type_desc_17_17, actionValue1.toInt())


        else -> UNKNOWN
    }

    return getString(R.string.skill_action_condition, desc)
}

// 42：触发
fun SkillActionDetail.loopTrigger() = when (actionDetail1) {
    2 -> {
        val value = getValueText(1, actionValue1, actionValue2, 0.0, percent = "%")

        getString(
            R.string.skill_action_type_desc_42_2,
            actionValue4.toInt(),
            value,
            actionDetail2 % 100
        )
    }

    14 -> {
        val value = getValueText(1, actionValue1, actionValue2, 0.0, percent = "%")
        var actionText =
            getString(id = R.string.skill_action_d, actionDetail2 % 100)
        if (actionDetail3 != 0) {
            actionText += "、" + getString(
                id = R.string.skill_action_d,
                actionDetail3 % 100
            )
        }
        getString(
            R.string.skill_action_type_desc_42_14,
            actionValue4.toInt(),
            value,
            actionText
        )
    }

    else -> UNKNOWN
}

//111：触发条件？??
fun SkillActionDetail.triggerV2(): String {
    val effect = when (actionDetail1) {
        1 -> getString(R.string.skill_action_type_desc_77_1) + getString(R.string.skill_buff)
        2 -> getString(R.string.skill_action_type_desc_77_1) + getString(R.string.skill_action_type_desc_111_2)
//        3 -> getString(R.string.skill_action_type_desc_77_1) + getString(R.string.skill_status_down)
//        4 -> getString(R.string.skill_status_ub)
        else -> UNKNOWN
    }
    return getString(
        R.string.skill_action_type_desc_111,
        getTarget(),
//        "<",
//        actionValue3.toInt(),
        effect,
        actionDetail2 % 100
    )
}
