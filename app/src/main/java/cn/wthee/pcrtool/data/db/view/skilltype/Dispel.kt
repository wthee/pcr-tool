package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getBarrierType
import cn.wthee.pcrtool.utils.getBuffText
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getValueText

/**
 * 49：移除增益
 *  @see <a href="https://github.com/wthee/pcr-tool/issues/154">issue #154</a>
 */
fun SkillActionDetail.dispel(): String {
    val value = getValueText(1, actionValue1, actionValue2, 0.0, percent = "%")
    //	BUFF = 1,
    //	DEBUFF = 2,
    //	STATUS_UP_CLEAR = 3,
    //	ALL_BARRIER = 10,
    //	GUARD_ATK_BARRIER = 11,
    //	GUARD_MGC_BARRIER = 12,
    //	DRAIN_ATK_BARRIER = 13,
    //	DRAIN_MGC_BARRIER = 14,
    //	GUARD_BOTH_BARRIER = 15,
    //	DRAIN_BOTH_BARRIER = 16,
    //	ALL_ATK_BARRIER = 17,
    //	ALL_MGC_BARRIER = 18,
    //	ALL_BOTH_BARRIER = 19,
    //	BUFF_DEBUFF_TYPE = 20
    val type = when (actionDetail1) {
        1 -> getString(R.string.skill_buff)
        2 -> getString(R.string.skill_debuff)
        3 -> getString(R.string.skill_buff_without_field)
        10 -> getString(R.string.skill_barrier)
        in 11..19 -> getBarrierType(actionDetail1 % 10)
        20 -> getBuffText(actionValue4.toInt()) + getString(R.string.skill_effect)
        else -> UNKNOWN
    }
    return getString(R.string.skill_action_type_desc_49, value, getTarget(), type)
}
