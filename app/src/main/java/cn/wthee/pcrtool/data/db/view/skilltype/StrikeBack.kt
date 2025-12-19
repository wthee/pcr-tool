package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getBarrierType
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getValueText

// 33：反伤
fun SkillActionDetail.strikeBack(): String {
    val value = getValueText(1, actionValue1, actionValue2)
    val type = getBarrierType(actionDetail1)
    val shieldText =
        getString(R.string.skill_action_type_desc_6, getTarget(), type, "", "")
    val backType = when (actionDetail1) {
        1, 3 -> getString(R.string.skill_physical)
        2, 4 -> getString(R.string.skill_magic)
        else -> ""
    }
    val hpRecovery = when (actionDetail1) {
        3, 4, 6 -> getString(R.string.skill_action_type_desc_33_hp)
        else -> ""
    }

    /**
     * @see <a href="https://github.com/wthee/pcr-tool/issues/147">issue #147</a>
     */
    val action = if (actionValue3.toInt() != 0) {
        // 动作
        getString(R.string.skill_action_type_desc_33_action, actionDetail3.toInt() % 100)
    } else {
        // 数值
        getString(R.string.skill_action_type_desc_33_value, value)
    }
    return if (actionDetail1 <= 6) {
        getString(
            R.string.skill_action_type_desc_33,
            shieldText,
            backType,
            action,
            hpRecovery,
            actionValue3.toInt()
        )
    } else {
        UNKNOWN
    }
}