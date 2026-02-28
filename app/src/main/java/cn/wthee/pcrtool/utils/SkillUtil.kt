package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.data.enums.BuffType
import cn.wthee.pcrtool.data.enums.SkillActionType


// 用于处理技能效果详情信息


/**
 * 伤害类型，根据actionDetail1判断
 */
fun SkillActionDetail.getAtkType() = getString(
    when (actionDetail1) {
        1 -> R.string.skill_physical
        2 -> R.string.skill_magic
        3 -> R.string.skill_must_hit_physical
        4 -> R.string.skill_must_hit_magic
        5 -> R.string.skill_adaptive_lower_defense_change_atk_type
        else -> R.string.unknown
    }
)

/**
 * 获取 %
 */
fun SkillActionDetail.getPercent() = when (SkillActionType.getByType(actionType)) {
    SkillActionType.AURA, SkillActionType.HEAL_DOWN -> {
        if (actionValue1.toInt() == 2 || actionDetail1 / 10 in setOf(
                BuffType.PHYSICAL_CRITICAL_DAMAGE.type,
                BuffType.MAGIC_CRITICAL_DAMAGE.type,
                BuffType.CRITICAL_DAMAGE_TAKE.type,
                BuffType.DAMAGE_TAKE.type,
                BuffType.PHYSICAL_DAMAGE_TAKE.type,
                BuffType.MAGIC_DAMAGE_TAKE.type,
                BuffType.PHYSICAL_DAMAGE.type,
                BuffType.MAGIC_DAMAGE.type
            )
        ) {
            "%"
        } else {
            ""
        }
    }

    SkillActionType.AURA_FIELD -> {
        if (actionDetail2 == 2 || actionDetail1 / 10 in setOf(
                BuffType.PHYSICAL_CRITICAL_DAMAGE.type,
                BuffType.MAGIC_CRITICAL_DAMAGE.type,
                BuffType.CRITICAL_DAMAGE_TAKE.type,
                BuffType.DAMAGE_TAKE.type,
                BuffType.PHYSICAL_DAMAGE_TAKE.type,
                BuffType.MAGIC_DAMAGE_TAKE.type,
                BuffType.PHYSICAL_DAMAGE.type,
                BuffType.MAGIC_DAMAGE.type
            )
        ) {
            "%"
        } else {
            ""
        }
    }
    //日服新吃货 actionDetail1 = 4、5 固定减伤
    SkillActionType.DAMAGE_REDUCE -> if (actionDetail1 <= 3) "%" else ""
    SkillActionType.ACTION_DOT -> if (actionDetail1 == 10) "%" else ""
    SkillActionType.DOT -> if (actionDetail1 == 11) "%" else ""
    else -> ""
}

/**
 * 持续时间
 */
fun SkillActionDetail.getTimeText(
    index: Int,
    v1: Double,
    v2: Double = 0.0,
    hideIndex: Boolean = false
) = getString(
    R.string.skill_effect_time,
    getValueText(index, v1, v2, hideIndex = hideIndex)
)


/**
 * 获取数值
 *
 * @param index v1 传的值 action_value_? -> index = ?
 * @param percent 显示 %
 * @param maxValue value显示的最大值
 */
fun SkillActionDetail.getValueText(
    index: Int,
    v1: Double,
    v2: Double,
    v3: Double = 0.0,
    v4: Double = 0.0,
    percent: String = "",
    hideIndex: Boolean = false,
    maxValue: Double? = null
): String {
    val skillLevelText = getString(R.string.skill_level_text)
    val skillAtkStrText = getString(R.string.skill_atk_text)
    var value = if (v3 == 0.0) {
        if (v1 == 0.0 && v2 != 0.0) {
            "[${(v2 * level).intStr}$percent] <{${index + 1}}${v2.intStr} * $skillLevelText>"
        } else if (v1 != 0.0 && v2 == 0.0) {
            "{${index}}[${v1.toBigDecimal().stripTrailingZeros().toPlainString()}$percent]"
        } else if (v1 != 0.0) {
            "[${(v1 + v2 * level).intStr}$percent] <{${index}}${v1.intStr} + {${index + 1}}${v2.intStr} * $skillLevelText>"
        } else {
            "{$index}[0]$percent"
        }
    } else {
        if (v4 != 0.0) {
            "[${(v1 + v2 * level + (v3 + v4 * level) * atk).intStr}$percent] <{${index}}${v1.intStr} + {${index + 1}}${v2.intStr} * $skillLevelText + ﹙{${index + 2}}${v3.intStr} + {${index + 3}}${v4.intStr} * $skillLevelText﹚ * $skillAtkStrText>"
        } else if (v1 == 0.0 && v2 != 0.0) {
            "[${(v2 + v3 * atk).intStr}$percent] <{${index + 1}}${v2.intStr} + {${index + 2}}${v3.intStr} * $skillAtkStrText>"
        } else if (v1 == 0.0) {
            "[${(v3 * atk).intStr}$percent] <{${index + 2}}${v3.intStr} * $skillAtkStrText>"
        } else if (v2 != 0.0) {
            "[${(v1 + v2 * level + v3 * atk).intStr}$percent] <{${index}}${v1.intStr} + {${index + 1}}${v2.intStr} * $skillLevelText + {${index + 2}}${v3.intStr} * $skillAtkStrText>"
        } else {
            "{$index}[0]$percent"
        }
    }
    value = if (maxValue != null) {
        value.replace(Regex("\\[.*?\\]"), "[${maxValue.intStr}$percent]")
    } else {
        value
    }
    return if (hideIndex) {
        value.replace(Regex("\\{.*?\\}"), "")
    } else {
        value
    }
}

/**
 * 效果
 *
 * @param value action_detail
 * @param valueText 数值描述文本
 * @param actionValue7 是否可驱散判断
 */
fun getBuffText(value: Int, valueText: String = "", actionValue7: Double = 0.0): String {
    //获取实际类型，1021 -> 2 10 -> 1
    val typeValue = value % 1000 / 10
    val buffText = getString(
        if (value == 1) {
            R.string.skill_hp_max
        } else {
            BuffType.getByValue(typeValue).nameId
        }
    )
    //提升/减少
    var changeDesc = when (BuffType.getByValue(typeValue)) {
        BuffType.CRITICAL_DAMAGE_TAKE,
        BuffType.DAMAGE_TAKE,
        BuffType.PHYSICAL_DAMAGE_TAKE,
        BuffType.MAGIC_DAMAGE_TAKE,
            -> {
            getString(
                if (value % 10 == 0) {
                    R.string.skill_reduce
                } else {
                    R.string.skill_increase
                }
            )
        }

        else -> getString(
            if (value % 10 == 0) {
                R.string.skill_increase
            } else {
                R.string.skill_reduce
            }
        )
    }

    //数值
    if (valueText != "") {
        changeDesc += " $valueText"
    }
    //固定buff，不受其他效果影响
    //参考(台服数据)： action_id = 217307201 action_detail_1 = 1020 description=提升自身的物理防禦力（不可降低）
    if (value / 1000 == 1) {
        changeDesc += getString(R.string.skill_fixed)
    }
    if (actionValue7.toInt() == 2) {
        changeDesc += getString(R.string.skill_cannot_dispel)
    }

    return buffText + changeDesc
}

/**
 * 护盾类型
 *
 */
fun getBarrierType(v1: Int): String {
    //	GUARD_ATK_BARRIER = 11,
    //	GUARD_MGC_BARRIER = 12,
    //	DRAIN_ATK_BARRIER = 13,
    //	DRAIN_MGC_BARRIER = 14,
    //	GUARD_BOTH_BARRIER = 15,
    //	DRAIN_BOTH_BARRIER = 16,
    //	ALL_ATK_BARRIER = 17,
    //	ALL_MGC_BARRIER = 18,
    //	ALL_BOTH_BARRIER = 19,

    //作用
    val function = when (v1) {
        1, 2, 5 -> {
            //无效
            getString(R.string.skill_barrier_no_effect)
        }

        3, 4, 6 -> {
            //吸收
            getString(R.string.skill_barrier_defense)
        }

        7, 8, 9 -> {
            //无效和吸收
            getString(R.string.skill_barrier_both)
        }

        else -> {
            getString(R.string.unknown)
        }
    }

    //类型
    val type = when (v1) {
        1, 3, 7 -> {
            getString(R.string.physical)
        }

        2, 4, 8 -> {
            getString(R.string.magic)
        }

        5, 6, 9 -> {
            getString(R.string.skill_all)
        }

        else -> {
            getString(R.string.unknown)
        }
    }

    return getString(R.string.skill_barrier_detail, function, type)
}

/**
 * 技能目标分配
 */
fun SkillActionDetail.getTargetAssignment() = getString(
    if (targetType == 7) {
        //target 类型为 7（仅自身），不再添加目标分配
        R.string.none
    } else {
        when (targetAssignment) {
            0 -> R.string.skill_target_assignment_0
            1 -> R.string.skill_target_assignment_1
            2 -> R.string.skill_target_assignment_2
            3 -> R.string.skill_target_assignment_3
            else -> R.string.none
        }
    }
)

/**
 * 首个目标位置
 */
fun SkillActionDetail.getTargetNumber(): String {
    val order = if (targetAssignment == 1) {
        when (targetNumber) {
            in 1..10 -> {
                getString(R.string.skill_target_order_num, targetNumber + 1)
            }

            else -> ""
        }
    } else {
        when (targetNumber) {
            1 -> getString(R.string.skill_target_order_1)
            in 2..10 -> {
                getString(R.string.skill_target_order_num, targetNumber)
            }

            else -> ""
        }
    }
    return if (order != "") {
        "⌈${order}⌋"
    } else {
        ""
    }
}

/**
 * 作用对象数量
 */
fun SkillActionDetail.getTargetCount() = when (targetCount) {
    0 -> ""
    1 -> {
        //目标是敌人时，显示生效目标数量
        if (targetAssignment == 1) {
            getString(R.string.skill_target_single)
        } else {
            ""
        }
    }

    99 -> getString(R.string.skill_target_all)
    else -> getString(R.string.skill_target_count, targetCount)
}

/**
 * 作用范围
 */
fun SkillActionDetail.getTargetRange() = when (targetRange) {
    in 1 until 2160 -> {
        val range = getString(R.string.skill_range, targetRange)
        "⌈${range}⌋"
    }

    else -> ""
}

/**
 * 目标类型
 *
 * @see <a href="https://github.com/wthee/pcr-tool/issues/148#issuecomment-3671555051">issue #148</a>
 */
fun SkillActionDetail.getTargetType(): String {
    val targetArea = when (targetArea) {
        4, 5, 6 -> getString(R.string.skill_area_include_flight)
        7, 8, 9 -> getString(R.string.skill_area_without_summon)
        else -> ""
    }
    val target = getString(
        when (targetType) {
            0, 1, 3, 40, 41 -> R.string.none
            2, 8 -> R.string.skill_target_2_8
//            3 -> {
//                //非范围技能时，显示生效目标类型：最近的
//                if(targetRange == 0 || targetRange == 2160){
//                    R.string.skill_target_3
//                }else{
//                    R.string.none
//                }
//            }
            4 -> R.string.skill_target_4
            5, 25 -> R.string.skill_target_5_25
            6, 26 -> R.string.skill_target_6_26
            7 -> R.string.skill_target_7
            9 -> R.string.skill_target_9
            10 -> R.string.skill_target_10
            11 -> R.string.skill_target_11
            12, 27, 37 -> R.string.skill_target_12_27_37
            13, 19, 28 -> R.string.skill_target_13_19_28
            14, 29 -> R.string.skill_target_14_29
            15, 30 -> R.string.skill_target_15_30
            16, 31 -> R.string.skill_target_16_31
            17, 32 -> R.string.skill_target_17_32
            18 -> R.string.skill_target_18
            20 -> R.string.skill_target_20
            21 -> R.string.skill_target_21
            22 -> R.string.skill_target_22
            23 -> R.string.skill_target_23
            24 -> R.string.skill_target_24
            33 -> R.string.skill_target_33
            34 -> R.string.skill_target_34
            35 -> R.string.skill_target_35
            36 -> R.string.skill_target_36
            38 -> R.string.skill_target_38
            39 -> R.string.skill_target_39
            42 -> R.string.skill_target_42
            43 -> R.string.skill_target_43
            44 -> R.string.skill_target_44
            45 -> R.string.skill_target_45
            46 -> R.string.skill_target_46
            47 -> R.string.skill_target_47
            50 -> R.string.skill_target_50
            51 -> R.string.skill_target_51
            in 13195..14000 -> R.string.skill_target_13xxx
            14001, 15001 -> R.string.skill_target_fire
            14002, 15002 -> R.string.skill_target_water
            14003, 15003 -> R.string.skill_target_wind
            14004, 15004 -> R.string.skill_target_light
            15005, 14005 -> R.string.skill_target_dark
            else -> R.string.unknown
        }
    )
    return if (target != "") {
        "⌈$targetArea$target⌋"
    } else if (targetArea != "") {
        "⌈$targetArea⌋"
    } else {
        ""
    }
}

/**
 * 获取目标具体描述
 */
fun SkillActionDetail.getTarget(): String {
    //依赖
    val depend = if (dependId != 0) {
        getString(R.string.skill_depend_action, dependId % 100)
    } else {
        ""
    }
    //范围
    val range = if (targetCount == 99 && targetRange == 2160 && actionValue6.toInt() == 1
        && actionValue7.toInt() == 0 && actionDetail2 == 1
    ) {
        // fixme 敌方友方均生效，判断逻辑
        getString(R.string.skill_target_assignment_3)
    } else {
        getTargetNumber() + getTargetRange() + getTargetAssignment()
    }

    return depend + getTargetType() + range + getTargetCount()
//            .replace("己方自身", "自身")
//            .replace("自身己方", "自身")
//            .replace("自身全体", "自身")
//            .replace("自身敌人", "自身")
}


/**
 * 获取技能附带的状态
 */
fun SkillActionDetail.getStatus(value: Int) = when (value) {
    100 -> getString(R.string.skill_status_100)
    101 -> getString(R.string.skill_status_101)
    200 -> getString(R.string.skill_status_200)
    300 -> getString(R.string.skill_status_300)
    400 -> getString(R.string.skill_status_400)
    500 -> getString(R.string.skill_status_500)
    501 -> getString(R.string.skill_status_501)
    502 -> getString(R.string.skill_status_502)
    503 -> getString(R.string.skill_status_503)
    504 -> getString(R.string.skill_status_504)
    511 -> getString(R.string.skill_status_511)
    512 -> getString(R.string.skill_status_512)
    710 -> getString(R.string.skill_status_710)
    900 -> getString(R.string.skill_status_900)
    1400 -> getString(R.string.skill_status_1400)
    1600 -> getString(R.string.skill_status_1600)
    1601 -> getString(R.string.skill_status_1601)
    1700 -> getString(R.string.skill_status_1700, getBuffText(actionValue3.toInt()))
    721 -> getString(R.string.skill_status_721)
    6107 -> getString(R.string.skill_status_6107)
    1513 -> getString(R.string.skill_ailment_13)
    1800 -> getString(R.string.skill_status_1800)
    1900 -> getString(R.string.skill_status_1900)
    3137 -> getString(R.string.skill_status_3137)
    3162 -> getString(R.string.skill_status_3162)
    3175 -> getString(R.string.skill_status_3175)
    3207 -> getString(R.string.skill_status_3207)
    6160 -> getString(R.string.skill_status_6160)
    4001 -> getString(R.string.skill_target_fire)
    4002 -> getString(R.string.skill_target_water)
    4003 -> getString(R.string.skill_target_wind)
    4004 -> getString(R.string.skill_target_light)
    4005 -> getString(R.string.skill_target_dark)
    else -> getString(R.string.unknown)
}


/**
 * 回避等技能限制
 */
fun SkillActionDetail.initOtherLimit() {
    if (level > Constants.OTHER_LIMIT_LEVEL && isOtherRfSkill) {
        isOtherLimitAction = true
    }
}

/**
 * 受击 tp 回复
 */
fun SkillActionDetail.takeDamageTp() = if (actionDetail3 != 0) {
    val multiple = 1 - actionDetail3 / 100
    if (multiple == 0) {
        //不回复 tp
        getString(R.string.skill_action_take_damage_tp_0)
    } else {
        getString(R.string.skill_action_take_damage_tp_multiple, multiple)
    }
} else {
    ""
}

/**
 * 增加或减少
 */
fun getEffectType(value: Int) = when (value) {
    1 -> getString(R.string.skill_action_type_desc_additive)
    2 -> getString(R.string.skill_action_type_desc_subtract)
    else -> getString(R.string.unknown)
}