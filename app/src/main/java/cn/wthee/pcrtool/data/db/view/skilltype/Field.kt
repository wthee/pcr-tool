package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getAtkType
import cn.wthee.pcrtool.utils.getBuffText
import cn.wthee.pcrtool.utils.getPercent
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 36：攻击领域展开
fun SkillActionDetail.attackField(): String {
    val atkType = getAtkType()
    val value = getValueText(
        1,
        actionValue1,
        actionValue2,
        actionValue3
    )
    val time = getTimeText(5, actionValue5, actionValue6)
    val damage = getString(R.string.skill_action_type_desc_36_damage, value, atkType)

    return getString(
        R.string.skill_action_type_desc_field,
        actionValue7.toInt(),
        damage,
        time
    )
}

// 37：治疗领域展开
fun SkillActionDetail.healField(): String {
    val value = getValueText(1, actionValue1, actionValue2, actionValue3)
    val heal = getString(R.string.skill_action_type_desc_37_heal, value)
    val time = getTimeText(5, actionValue5, actionValue6)

    return getString(R.string.skill_action_type_desc_field, actionValue7.toInt(), heal, time)
}

// 38：buff/debuff领域展开
fun SkillActionDetail.auraField(): String {
    val value = getValueText(1, actionValue1, actionValue2, percent = getPercent())
    val time = getTimeText(3, actionValue3, actionValue4)
    val aura = getBuffText(actionDetail1, value, actionValue7)

    return getTarget() + getString(
        R.string.skill_action_type_desc_field,
        actionValue5.toInt(),
        aura,
        time
    )
}

// 39：持续伤害领域展开
fun SkillActionDetail.dotField(): String {
    val time = getTimeText(1, actionValue1, actionValue2)
    val action =
        getString(R.string.skill_action_type_desc_38_action, actionDetail1 % 100)

    return getTarget() + getString(
        R.string.skill_action_type_desc_field,
        actionValue3.toInt(),
        action,
        time
    )
}

// 53：特殊状态：领域存在时；如：情姐
fun SkillActionDetail.ifHasField(): String {
    val content = if (actionDetail2 != 0 && actionDetail3 != 0) {
        val otherwise =
            getString(R.string.skill_action_type_desc_53_2, actionDetail3 % 100)
        getString(R.string.skill_action_type_desc_53, actionDetail2 % 100, otherwise)
    } else if (actionDetail2 != 0) {
        getString(R.string.skill_action_type_desc_53, actionDetail2 % 100, "")
    } else {
        UNKNOWN
    }
    return getString(R.string.skill_action_condition, content)
}

// 58：解除领域 如：晶姐 UB
fun SkillActionDetail.stopField() = getString(
    R.string.skill_action_type_desc_58,
    actionDetail1 / 100 % 10,
    actionDetail1 % 100
)

// 96：范围tp回复
fun SkillActionDetail.tpField(): String {
    val value = getValueText(1, actionValue1, actionValue2)
    val tp = getString(R.string.skill_action_type_desc_96_tp, value)
    val time = getTimeText(3, actionValue3, actionValue4)

    return getString(R.string.skill_action_type_desc_field, actionValue5.toInt(), tp, time)
}