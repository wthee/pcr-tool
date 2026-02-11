package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getValueText

// 90：EX被动 fixme 完善被动技能逻辑
fun SkillActionDetail.ex(): String {
    //NONE = 0;
    //DODGE = 8;
    //LIFE_STEAL = 9;
    //WAVE_HP_RECOVERY = 10;
    //WAVE_ENERGY_RECOVERY = 11;
    //PHYSICAL_PENETRATE = 12;
    //MAGIC_PENETRATE = 13;
    //ENERGY_RECOVERY_RATE = 14;
    //HP_RECOVERY_RATE = 15;
    //ENERGY_REDUCE_RATE = 16;
    //ACCURACY = 17;
    //BASE_PARAMETER_NUM = 18;
    //PHYSICAL_CRITICAL_DAMAGE_RATE = 100;
    //MAGIC_CRITICAL_DAMAGE_RATE = 101;
    //PHYSICAL_DAMAGE_UP_PERCENT = 102;
    //MAGIC_DAMAGE_UP_PERCENT = 103;
    //TALENT_BONUS = 104;
    //ABNORMAL_STATE_DODGE_HIT = 105;
    //ABNORMAL_STATE_DODGE_RESIST = 106;
    //ATTACK_ENHANCE = 107;
    //BUFF_ENHANCE = 108;
    //DEBUFF_ENHANCE = 109;
    //HEAL_ENHANCE = 110;
    //ENERGY_CHARGE = 111;
    val type = when (actionDetail1) {
        1 -> getString(R.string.attr_hp)
        2 -> getString(R.string.attr_atk)
        3 -> getString(R.string.attr_def)
        4 -> getString(R.string.attr_magic_str)
        5 -> getString(R.string.attr_magic_def)
        6 -> getString(R.string.attr_physical_critical)
        7 -> getString(R.string.attr_magic_critical)
        else -> getString(R.string.unknown)
    }
    val value = getValueText(2, actionValue2, actionValue3)

    return getString(R.string.skill_action_type_desc_90, getTarget(), type, value)
}