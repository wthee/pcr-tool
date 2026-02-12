package cn.wthee.pcrtool.data.enums

import cn.wthee.pcrtool.R

/**
 * 属性
 */
enum class BuffType(val type: Int, val nameId: Int) {
    UNKNOWN(-1, R.string.unknown),
    ATK(1, R.string.attr_atk),
    DEF(2, R.string.attr_def),
    MAGIC_STR(3, R.string.attr_magic_str),
    MAGIC_DEF(4, R.string.attr_magic_def),
    DODGE(5, R.string.attr_dodge),
    PHYSICAL_CRITICAL(6, R.string.attr_physical_critical),
    MAGIC_CRITICAL(7, R.string.attr_magic_critical),
    ENERGY_RECOVERY_RATE(8, R.string.attr_energy_recovery_rate),
    LIFE_STEAL(9, R.string.attr_life_steal),
    SPEED(10, R.string.skill_speed),
    PHYSICAL_CRITICAL_DAMAGE(11, R.string.skill_physical_critical_damage),
    MAGIC_CRITICAL_DAMAGE(12, R.string.skill_magic_critical_damage),
    ACCURACY(13, R.string.attr_accuracy),
    CRITICAL_DAMAGE_TAKE(14, R.string.skill_critical_damage_take),
    DAMAGE_TAKE(15, R.string.skill_damage_take),
    PHYSICAL_DAMAGE_TAKE(16, R.string.skill_physical_damage_take),
    MAGIC_DAMAGE_TAKE(17, R.string.skill_magic_damage_take),
    PHYSICAL_DAMAGE(18, R.string.skill_physical_damage),
    MAGIC_DAMAGE(19, R.string.skill_magic_damage),
    BUFF(20, R.string.skill_buff_effect),
    MAX_HP(100, R.string.skill_hp_max),
    ;

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.type == value } ?: UNKNOWN
    }
}