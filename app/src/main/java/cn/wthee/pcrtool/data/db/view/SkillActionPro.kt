package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.data.enums.SkillActionType
import cn.wthee.pcrtool.data.enums.getAilment
import cn.wthee.pcrtool.data.enums.toSkillActionType
import cn.wthee.pcrtool.utils.getZhNumberText
import cn.wthee.pcrtool.utils.int
import kotlin.math.abs


/**
 * 技能效果
 */
data class SkillActionPro(
    @PrimaryKey
    @ColumnInfo(name = "lv") var level: Int = 0,
    @ColumnInfo(name = "atk") var atk: Int = 0,
    @ColumnInfo(name = "action_id") var action_id: Int = 0,
    @ColumnInfo(name = "class_id") var class_id: Int = 0,
    @ColumnInfo(name = "action_type") var action_type: Int = 0,
    @ColumnInfo(name = "action_detail_1") var action_detail_1: Int = 0,
    @ColumnInfo(name = "action_detail_2") var action_detail_2: Int = 0,
    @ColumnInfo(name = "action_detail_3") var action_detail_3: Int = 0,
    @ColumnInfo(name = "action_value_1") var action_value_1: Double = 0.0,
    @ColumnInfo(name = "action_value_2") var action_value_2: Double = 0.0,
    @ColumnInfo(name = "action_value_3") var action_value_3: Double = 0.0,
    @ColumnInfo(name = "action_value_4") var action_value_4: Double = 0.0,
    @ColumnInfo(name = "action_value_5") var action_value_5: Double = 0.0,
    @ColumnInfo(name = "action_value_6") var action_value_6: Double = 0.0,
    @ColumnInfo(name = "action_value_7") var action_value_7: Double = 0.0,
    @ColumnInfo(name = "target_assignment") var target_assignment: Int = 0,
    @ColumnInfo(name = "target_area") var target_area: Int = 0,
    @ColumnInfo(name = "target_range") var target_range: Int = 0,
    @ColumnInfo(name = "target_type") var target_type: Int = 0,
    @ColumnInfo(name = "target_number") var target_number: Int = 0,
    @ColumnInfo(name = "target_count") var target_count: Int = 0,
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "level_up_disp") var level_up_disp: String = "",
    @ColumnInfo(name = "ailment_name") var tag: String,
    @Ignore
    var dependId: Int = 0
) {

    constructor() : this(
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0,
        0,
        0,
        0,
        0,
        0,
        "",
        "0",
        "0",
        0
    )

    /**
     * 依赖技能
     */
    private fun getDependAction() = if (dependId != 0) "受到动作(${dependId % 10})影响的" else ""

    /**
     * 技能目标
     */
    private fun getTargetAssignment() = when (target_assignment) {
        0 -> "自身"
        1 -> "敌人"
        2 -> "己方"
        3 -> "敌人和己方"
        else -> ""
    }

    /**
     * 首个目标位置
     */
    private fun getTargetNumber() = when (target_number) {
        in 1..10 -> "(第${getZhNumberText(target_number + 1)}近)"
        else -> ""
    }

    /**
     * 作用对象数量
     */
    private fun getTargetCount() = when (target_count) {
        0, 1 -> ""
        99 -> "全体"
        else -> "($target_count)"
    }

    /**
     * 作用范围
     */
    private fun getTargetRange() = when (target_range) {
        in 1 until 2160 -> "范围($target_range)内"
        else -> ""
    }

    /**
     * fixme 目标类型
     */
    private fun getTargetType() = when (target_type) {
        0, 1, 3 -> ""
        2, 8 -> "随机的"
        4 -> "最远的"
        5, 26 -> "HP比例最低的"
        6, 25 -> "HP比例最高的"
        7 -> "自身"
        9 -> "最前方的"
        10 -> "最后方的"
        11 -> "范围内的"
        12, 27, 37 -> "TP最高的"
        13, 19, 28 -> "TP最低的"
        14, 29 -> "物理攻击力最高的"
        15, 30 -> "物理攻击力最低的"
        16, 31 -> "魔法攻击力最高的"
        17, 32 -> "魔法攻击力最低的"
        18 -> "召唤物"
        20 -> "物理攻击的"
        21 -> "魔法攻击的"
        22 -> "随机的召唤物"
        23 -> "自身的随机召唤物"
        24 -> "领主"
        33 -> "暗影"
        34 -> "除自身以外"
        35 -> "HP最高的"
        36 -> "HP最低的"
        38 -> "物理或魔法攻击力最高的"
        39 -> "物理或魔法攻击力最低的"
        else -> ""
    }

    private fun getTarget(): String {
        return (getTargetType() + getTargetNumber() + getTargetRange() + getDependAction() + getTargetAssignment() + getTargetCount())
            .replace("己方自身", "自身")
            .replace("自身己方", "自身")
            .replace("自身全体", "自身")

    }


    /**
     * 获取技能效果
     *
     * 技能效果判断逻辑来源 @author MalitsPlus[https://github.com/MalitsPlus]
     *
     */
    fun getActionDesc(): SkillActionText? {
//        if (isEmptyAction()) {
//            return null
//        }
        //设置状态标签
        val p = getAilment(action_type)
        if (p.isNotEmpty()) {
            tag = p
        }

        var summonUnitId = 0
        val status = when (action_detail_1) {
            100 -> "无法行动"
            200 -> "失明"
            300 -> "魅惑状态"
            400 -> "挑衅状态"
            500 -> "烧伤状态"
            501 -> "诅咒状态"
            502 -> "中毒状态"
            503 -> "猛毒状态"
            512 -> "中毒或猛毒状态"
            710 -> "BREAK 状态"
            1400 -> "变身状态"
            else -> ""
        }

        val formatDesc = when (toSkillActionType(action_type)) {
            SkillActionType.DAMAGE -> {
                val atkType = when (action_detail_1) {
                    1 -> "物理"
                    2 -> "魔法"
                    else -> ""
                }
                val value = getValueText(1, action_value_1, action_value_2, action_value_3)
                "对${getTarget()}造成 $value 的${atkType}伤害"
            }
            SkillActionType.MOVE -> {
                val directionText = if (action_value_1 > 0) "向前" else "向后"
                val positionText = if (action_value_1 > 0) "前方" else "后方"
                val moveText = "移动至${getTarget()}前[$action_value_1]"
                val returnText = "，动作结束后回到原来位置"
                val speedText = "，移动速度 [${action_value_2}] "
                when (action_detail_1) {
                    //移动后返回
                    1 -> moveText + returnText
                    //前、后移动后返回
                    2 -> directionText + moveText + returnText
                    //移动
                    3 -> moveText
                    //方向
                    4, 7 -> directionText + moveText
                    //方向、速度
                    5 -> moveText + positionText + speedText
                    6 -> directionText + moveText + speedText
                    else -> ""
                }

            }
            SkillActionType.CHANGE_ENEMY_POSITION -> {
                when (action_detail_1) {
                    1 -> {
                        tag = "击飞"
                        "${tag}${getTarget()}，高度[${(abs(action_value_1)).int}]"
                    }
                    3, 6 -> {
                        tag = if (action_value_1 > 0) "击退" else "拉近"
                        "${tag}${getTarget()}，距离[${(abs(action_value_1)).int}]"
                    }
                    else -> ""
                }
            }
            SkillActionType.HEAL -> {
                val value = getValueText(2, action_value_2, action_value_3, action_value_4)
                "使${getTarget()}HP回复 $value"
            }
            SkillActionType.CURE -> ""
            SkillActionType.BARRIER -> {
                val value = getValueText(1, action_value_1, action_value_2)
                val time = getTimeText(action_value_3, action_value_4)
                val type = getBarrierTtpe(action_detail_1)
                if (type != "") {
                    "对${getTarget()}展开${type}${value}${time}"
                } else {
                    ""
                }
            }
            SkillActionType.CHOOSE_ENEMY -> {
                "视点切换到${getTarget()}"
            }
            SkillActionType.CHANGE_ACTION_SPEED -> {
                //判断异常状态
                tag = when (action_detail_1) {
                    1 -> "减速"
                    2 -> "加速"
                    3 -> "麻痹"
                    4 -> "冰冻"
                    5 -> "束缚"
                    6 -> "睡眠"
                    7 -> "眩晕"
                    8 -> "石化"
                    9 -> "拘留"
                    10 -> "晕倒"
                    11 -> "时停"
                    else -> "？"
                }
                val value = getValueText(1, action_value_1, action_value_2)
                val time = getTimeText(action_value_3, action_value_4)
                val main = when (action_detail_1) {
                    1, 2 -> "${tag}${getTarget()}，速度 * ${value}$time"
                    else -> "使${getTarget()}进入${tag}状态$time"
                }
                if (action_detail_2 == 1) {
                    "$main，本效果将会在受到伤害时解除"
                } else {
                    main
                }
            }
            SkillActionType.DOT -> {
                tag = when (action_detail_1) {
                    0 -> "拘留（造成伤害）"
                    1 -> "中毒"
                    2 -> "烧伤"
                    3, 5 -> "诅咒"
                    4 -> "猛毒"
                    else -> "?"
                }
                val value = getValueText(1, action_value_1, action_value_2)
                val time = getTimeText(action_value_3, action_value_4)
                "使${getTarget()}进入${tag}状态，每秒造成伤害 $value $time"
            }
            SkillActionType.AURA -> {
                tag = if (action_detail_1 % 10 == 0) "BUFF" else "DEBUFF"
                val aura = getAura(action_detail_1)
                val percent = if (action_value_1.toInt() == 1) "" else "%"
                val value = getValueText(2, action_value_2, action_value_3, 0.0, percent)
                val time = getTimeText(action_value_4, action_value_5)
                if (action_detail_2 == 2) {
                    "BREAK 期间，"
                } else {
                    ""
                } + "${getTarget()}${aura} $value $time"

            }
            SkillActionType.CHARM -> {
                tag = when (action_detail_1) {
                    0 -> "魅惑"
                    1 -> "混乱"
                    else -> "?"
                }
                val time = getTimeText(action_value_1, action_value_2)
                val chance =
                    if (action_value_3 == 100.toDouble()) "成功率 [100] " else "成功率 [${(1 + action_value_3 * level).int}] <1 + $action_value_3 * 技能等级> "
                "${tag}${getTarget()}${time}，$chance"
            }
            SkillActionType.BLIND -> {
                val chance = getValueText(3, action_value_3, action_value_4, 0.0, "%")
                val time = getTimeText(action_value_1, action_value_2)
                "以 $chance 的概率使${getTarget()}进入${toSkillActionType(action_type).desc}状态${time}。对象进行物理攻击时有 [${100 - action_detail_1}%] 的概率miss"
            }
            SkillActionType.SILENCE -> {
                val chance = getValueText(3, action_value_3, action_value_4, 0.0, "%")
                val time = getTimeText(action_value_1, action_value_2)
                "以 $chance 的概率使${getTarget()}进入${toSkillActionType(action_type).desc}状态$time"

            }
            SkillActionType.CHANGE_MODE -> {
                when (action_detail_1) {
                    1 -> "技能循环改变，持续 [${action_value_1}] 秒"
                    2 -> "技能循环改变，每秒降低 TP[${action_value_1}]"
                    3 -> "效果结束后，切换回技能循环"
                    else -> "?"
                }
            }
            SkillActionType.SUMMON -> {
                summonUnitId = action_detail_2
                when {
                    action_value_7 > 0 -> {
                        "在${getTarget()}前方 [${action_value_7.toInt()}] 的位置，召唤友方单位"
                    }
                    action_value_7 < 0 -> {
                        "在${getTarget()}后方 [${abs(action_value_7).int}] 的位置，召唤友方单位"
                    }
                    else -> {
                        "在${getTarget()}，召唤 [${action_detail_2}] 的召唤物。"
                    }
                }
            }
            SkillActionType.CHANGE_TP -> {
                val expr = if (action_value_2 > 0)
                    "[${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级>"
                else
                    "[${(action_value_1).int}]"
                tag = when (action_detail_1) {
                    1 -> "TP回复"
                    else -> "TP减少"
                }
                "${getTarget()}${tag} $expr"
            }
            SkillActionType.TRIGGER -> {
                val expr = when (action_detail_1) {
                    2 -> "受到伤害时 [${action_value_1.toInt()}%] 概率"
                    3 -> "HP[${action_value_3.toInt()}%] 以下"
                    4 -> "死亡时 [${action_value_1.toInt()}%] 概率"
                    5 -> "暴击时 [${action_value_1.toInt()}%] 概率"
                    7 -> "战斗剩余时间 [${action_value_3.toInt()}] 秒以下"
                    8 -> "潜伏时 [${action_value_1.toInt()}%] 概率"
                    9 -> "BREAK 时 [${action_value_1.toInt()}%] 的概率，持续 [${action_value_3.toInt()}] 秒"
                    10 -> "受到持续伤害时 [${action_value_1.toInt()}%] 概率"
                    11 -> "所有部位 BREAK"
                    else -> "未知"
                }
                "条件：$expr"
            }
            SkillActionType.CHARGE, SkillActionType.DAMAGE_CHARGE -> {
                "蓄力 [${action_value_3}] 秒" + when {
                    action_detail_2 > 0 -> {
                        "，下一个动作的效果增加 [${(action_value_1 + action_value_2 * level)}] <$action_value_1 + $action_value_2 * 技能等级> * 蓄力中受到的伤害"
                    }
                    action_value_1 > 0 -> {
                        "，下一个动作的效果增加 [${action_value_1}] * 蓄力中受到的伤害"
                    }
                    else -> {
                        ""
                    }
                }
            }
            SkillActionType.TAUNT -> {
                "使${getTarget()}进入${toSkillActionType(action_type).desc}状态${
                    getTimeText(
                        action_value_1,
                        action_value_2
                    )
                }"
            }
            SkillActionType.INVINCIBLE -> {
                tag = when (action_detail_1) {
                    1 -> "无敌"
                    2 -> "回避物理攻击"
                    3 -> "回避所有攻击"
                    else -> "?"
                }
                "${tag}${getTimeText(action_value_1, action_value_2)}"
            }
            SkillActionType.CHANGE_PATTERN -> {
                when (action_detail_1) {
                    1 -> if (action_value_1 > 0) {
                        "技能循环改变，持续 [${action_value_1}] 秒"
                    } else {
                        "技能循环改变"
                    }
                    2 -> "技能动画改变，持续 [${action_value_1}] 秒"
                    else -> "?"
                }
            }
            //fixme 优化描述、判断逻辑
            SkillActionType.IF_FOR_CHILDREN -> {
                var trueClause = ""
                var falseClause = ""
                if (action_detail_2 != 0) {
                    trueClause = if (status != "") {
                        "当${getTarget()}在[${status}]时，使用动作(${action_detail_2 % 100})"
                    } else {
                        if ((action_detail_1 in 600..699) || action_detail_1 == 710) {
                            "${getTarget()}持有标记时，使用动作(${action_detail_2 % 100})"
                        } else if (action_detail_1 == 700) {
                            "${getTarget()}是单独时，使用 [${action_detail_2 % 100}]"
                        } else if (action_detail_1 in 901..999) {
                            "${getTarget()}的HP在 [${action_detail_1 - 900}%] 以下时，使用动作(${action_detail_2 % 100})"
                        } else if (action_detail_1 == 1300) {
                            "${getTarget()}是使用魔法攻击对象时，使用动作(${action_detail_3 % 10})"
                        } else {
                            ""
                        }
                    }
                }
                if (action_detail_3 != 0) {
                    falseClause = if (status != "") {
                        "当${getTarget()}不在[${status}]时，使用动作(${action_detail_3 % 100})"
                    } else {
                        if ((action_detail_1 in 600..699) || action_detail_1 == 710) {
                            "${getTarget()}未持有标记时，使用动作(${action_detail_3 % 100})"
                        } else if (action_detail_1 == 700) {
                            "${getTarget()}不是单独时，使用 [${action_detail_3 % 100}]"
                        } else if (action_detail_1 in 901..999) {
                            "${getTarget()}的HP在 [${action_detail_1 - 900}%] 以上时，使用动作(${action_detail_3 % 100})"
                        } else if (action_detail_1 == 1300) {
                            "${getTarget()}不是使用魔法攻击对象时，使用动作(${action_detail_2 % 10})"
                        } else {
                            ""
                        }
                    }
                }
                //条件
                if (action_detail_1 == 100 || action_detail_1 == 200 || action_detail_1 == 300 || action_detail_1 == 500 || action_detail_1 == 501
                    || action_detail_1 == 502 || action_detail_1 == 503 || action_detail_1 == 512
                    || (action_detail_1 in 600..899) || (action_detail_1 in 901..999)
                    || action_detail_1 == 1300 || action_detail_1 == 1400
                ) {
                    if (trueClause != "" && falseClause != "")
                        "条件：${trueClause}；${falseClause}"
                    else if (trueClause != "")
                        "条件：${trueClause}"
                    else if (falseClause != "")
                        "条件：${falseClause}"
                    else
                        ""
                } else if (action_detail_1 in 0..99) {
                    if (action_detail_2 != 0 && action_detail_3 != 0) {
                        "随机事件：[${action_detail_1}%] 的概率使用动作(${action_detail_2 % 10})，否则使用动作(${action_detail_3 % 10})"
                    } else if (action_detail_2 != 0) {
                        "随机事件：[${action_detail_1}%] 的概率使用动作(${action_detail_2 % 10})"
                    } else if (action_detail_3 != 0) {
                        "随机事件：[${100 - action_detail_1}%] 的概率使用动作(${action_detail_2 % 10})"
                    } else {
                        ""
                    }
                } else {
                    ""
                }
            }
            SkillActionType.IF_FOR_ALL -> {
                var trueClause = ""
                var falseClause = ""
                if (action_detail_2 != 0) {
                    trueClause =
                        if (action_detail_1 == 710 || action_detail_1 == 100 || action_detail_1 in 500..512) {
                            if (status != "")
                                "当${getTarget()}在[${status}]时，使用动作(${action_detail_2 % 100})"
                            else
                                ""
                        } else if (action_detail_1 in 0..99) {
                            "以 [$action_detail_1%] 的概率使用动作(${action_detail_2 % 10})"
                        } else if (action_detail_1 == 599) {
                            "${getTarget()}身上有持续伤害时，使用动作(${action_detail_2 % 10})"
                        } else if (action_detail_1 in 600..699) {
                            "${getTarget()}的标记层数在 [${action_value_3.toInt()}] 以上时，使用动作(${action_detail_2 % 10})"
                        } else if (action_detail_1 == 700) {
                            "${getTarget()}是单独时，使用 [${action_detail_2 % 10}]"
                        } else if (action_detail_1 in 701..709) {
                            "排除潜伏状态的单位，${getTarget()}的数量是 [${action_detail_1 - 700}] 时，使用动作(${action_detail_2 % 10})"
                        } else if (action_detail_1 == 720) {
                            "排除潜伏状态的单位，${getTarget()}中存在 [ID: ${action_value_3.toInt()}] 的单位时，使用动作(${action_detail_2 % 10})"
                        } else if (action_detail_1 in 901..999) {
                            "${getTarget()}的HP在 [${action_detail_1 - 900}%] 以下时，使用动作(${action_detail_2 % 10})"
                        } else if (action_detail_1 == 1000) {
                            "上一个动作击杀了单位时，使用动作(${action_detail_2 % 10})"
                        } else if (action_detail_1 == 1001) {
                            "本技能触发暴击时，使用动作(${action_detail_2 % 10})"
                        } else if (action_detail_1 in 1200..1299) {
                            "[计数器 ${action_detail_1 % 100 / 10}] 的数量在 [${action_detail_1 % 10}] 以上时，使用动作(${action_detail_2 % 10})"
                        } else {
                            ""
                        }
                }

                if (action_detail_3 != 0) {
                    falseClause = if (action_detail_1 == 710 || action_detail_1 in 500..512) {
                        if (status != "")
                            "当${getTarget()}不在[${status}]时，使用动作(${action_detail_3 % 100})"
                        else
                            ""
                    } else if (action_detail_1 in 0..99) {
                        "以 [${100 - action_detail_1}%] 的概率使用动作(${action_detail_3 % 10})"
                    } else if (action_detail_1 == 599) {
                        "${getTarget()}身上没有持续伤害时，使用动作(${action_detail_3 % 10})"
                    } else if (action_detail_1 in 600..699) {
                        "${getTarget()}的标记层数不足 [${action_value_3.toInt()}] 时，使用动作(${action_detail_3 % 10})"
                    } else if (action_detail_1 == 700) {
                        "${getTarget()}是不是单独时，使用 [${action_detail_3 % 10}]"
                    } else if (action_detail_1 in 701..709) {
                        "排除潜伏状态的单位，${getTarget()}的数量不是 [${action_detail_1 - 700}] 时，使用动作(${action_detail_3 % 10})"
                    } else if (action_detail_1 == 720) {
                        "排除潜伏状态的单位，${getTarget()}中不存在 [ID: ${action_value_3.toInt()}] 的单位时，使用动作(${action_detail_3 % 10})"
                    } else if (action_detail_1 in 901..999) {
                        "${getTarget()}的HP在 [${action_detail_1 - 900}%] 以上时，使用动作(${action_detail_3 % 10})"
                    } else if (action_detail_1 == 1000) {
                        "上一个动作未击杀单位时，使用动作(${action_detail_3 % 10})"
                    } else if (action_detail_1 == 1001) {
                        "本技能未触发暴击时，使用动作(${action_detail_3 % 10})"
                    } else if (action_detail_1 in 1200..1299) {
                        "[计数器 ${action_detail_1 % 100 / 10}] 的数量不足 [${action_detail_1 % 10}] 时，使用动作(${action_detail_3 % 10})"
                    } else {
                        ""
                    }
                }

                //条件
                if (trueClause != "" && falseClause != "")
                    "条件：${trueClause}${falseClause}"
                else if (trueClause != "")
                    "条件：${trueClause}"
                else if (falseClause != "")
                    "条件：${falseClause}"
                else
                    ""
            }
            SkillActionType.REVIVAL -> {
                "复活${getTarget()}，并回复其 [${(action_value_2 * 100).int}]HP"
            }
            SkillActionType.CONTINUOUS_ATTACK -> ""
            SkillActionType.ADDITIVE -> {
                val type = when (action_value_1.toInt()) {
                    7 -> "物理攻击力"
                    8 -> "魔法攻击力"
                    9 -> "物理防御力"
                    10 -> "魔法防御力"
                    else -> "?"
                }
                val commonExpr = getValueText(2, action_value_2, action_value_3)
                val commonDesc =
                    "动作(${action_detail_1 % 10}) 的{${action_detail_2}} 增加 $commonExpr"

                val additive = when (action_value_1.toInt()) {
                    2 -> {
                        val expr = when {
                            action_detail_3 == 0 -> {
                                "[${action_value_2}]"
                            }
                            action_detail_2 == 0 -> {
                                "[${action_value_3}]"
                            }
                            else -> {
                                "[${(action_value_2 + 2 * action_value_3 * level)}] <$action_value_2 + ${2 * action_value_3} * 技能等级> "
                            }
                        }
                        "动作(${action_detail_1 % 10}) 的{${action_detail_2}} 增加 $expr * [击杀数量]"
                    }
                    0 -> "$commonDesc * [剩余的HP]"
                    1 -> "$commonDesc * [损失的HP]"
                    4 -> "$commonDesc * [目标的数量]"
                    5 -> "$commonDesc * [受到伤害的目标数量]"
                    6 -> "$commonDesc * [造成的伤害]"
                    12 -> "$commonDesc * [后方${getTarget()}数量]"
                    102 -> "$commonDesc * [小眼球数量]"
                    in 200 until 300 -> "$commonDesc * [标记的层数]"
                    in 7..10 -> "$commonDesc * [${getTarget()}的$type]"
                    in 20 until 30 -> "$commonDesc * [计数器${action_value_1.toInt() % 10}的数量]"
                    else -> "?"
                }
                additive
                //fixme 上限判断
//                val limit = "提高值的上限为 [%s]"
//                if (action_value_4.toInt() != 0 && action_value_5.toInt() != 0)
//                    desc + limit
//                else
//                    desc
            }
            SkillActionType.MULTIPLE, SkillActionType.DIVIDE -> {
                val commonExpr = getValueText(2, action_value_2, action_value_3)
                val commonDesc =
                    "动作(${action_detail_1 % 10}) 的{${action_detail_2}} 增加 $commonExpr"

                when (action_value_1.toInt()) {
                    2 -> {
                        val expr = when {
                            action_detail_3 == 0 -> {
                                "[${action_value_2}]"
                            }
                            action_detail_2 == 0 -> {
                                "[${action_value_3}]"
                            }
                            else -> {
                                "[${(action_value_2 + 2 * action_value_3 * level)}] <$action_value_2 + ${2 * action_value_3} * 技能等级> "
                            }
                        }
                        "动作(${action_detail_1 % 10}) 的{${action_detail_2}} 增加 $expr * [击杀数量]"
                    }
                    0 -> "$commonDesc * [HP]"
                    1 -> "$commonDesc * [损失的HP]"
                    in 200 until 300 -> "$commonDesc * [标记的层数]"
                    else -> "?"
                }
            }
            SkillActionType.CHANGE_SEARCH_AREA -> ""
            SkillActionType.KILL_ME -> "${getTargetType()}死亡"
            SkillActionType.CONTINUOUS_ATTACK_NEARBY -> ""
            SkillActionType.LIFE_STEAL -> {
                "为${getTarget()}的下 [${action_value_3.toInt()}] 次攻击附加 ${toSkillActionType(action_type).desc}[${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> 效果"
            }
            SkillActionType.STRIKE_BACK -> {
                val value = getValueText(1, action_value_1, action_value_2)
                val type = getBarrierTtpe(action_detail_1)
                val shieldText = "对${getTarget()}展开${type}${value}"

                when (action_detail_1) {
                    1 -> "${shieldText}，受到物理伤害时反射 [${action_value_3}] 倍伤害"
                    2 -> "${shieldText}，受到魔法伤害时反射 [${action_value_3}] 倍伤害"
                    3 -> "${shieldText}，受到物理伤害时反射 [${action_value_3}] 倍伤害，并回复HP"
                    4 -> "${shieldText}，受到魔法伤害时反射 [${action_value_3}] 倍伤害，并回复HP"
                    5 -> "${shieldText}，受到伤害时反射 [${action_value_3}] 倍伤害"
                    6 -> "${shieldText}，受到伤害时反射 [${action_value_3}] 倍伤害，并回复HP"
                    else -> "?"
                }
            }
            SkillActionType.ACCUMULATIVE_DAMAGE -> {
                "每次攻击当前的目标，将会追加 [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> , 叠加上限 [${(action_value_4).int}]"
            }
            SkillActionType.SEAL -> {
                if (action_value_4.toInt() > 0) {
                    val time = getTimeText(action_value_3)
                    "对${getTarget()}追加 [${action_value_4.toInt()}] 层标记${time}，叠加上限 [${action_value_1.toInt()}]"
                } else {
                    "${getTarget()}的标记层数减少 [${abs(action_value_4).toInt()}] 层"
                }
            }
            SkillActionType.ATTACK_FIELD -> {
                val atkType = if (action_detail_1 % 2 == 0) "魔法" else "物理"
                val value = "，每秒造成 ${
                    getValueText(
                        1,
                        action_value_1,
                        action_value_2,
                        action_value_3
                    )
                } ${atkType}伤害"
                "展开半径为 [${action_value_7.toInt()}] 的领域${value}${
                    getTimeText(
                        action_value_5,
                        action_value_6
                    )
                }"
            }
            SkillActionType.HEAL_FIELD -> {
                val value =
                    "，每秒回复 ${getValueText(1, action_value_1, action_value_2, action_value_3)}HP"
                "展开半径为 [${action_value_7.toInt()}] 的领域${value}${
                    getTimeText(
                        action_value_5,
                        action_value_6
                    )
                }"
            }
            SkillActionType.AURA_FIELD -> {
                val percent = if (action_detail_2 == 1) "" else "%"
                val value = getValueText(1, action_value_1, action_value_2, percent = percent)
                val time = getTimeText(action_value_3, action_value_4)
                "${getTarget()}展开半径为 [${action_value_5.toInt()}] 的领域，${getAura(action_detail_1)} ${value}$time"
            }
            SkillActionType.DOT_FIELD -> {
                val time = getTimeText(action_value_1, action_value_2)
                "${getTarget()}展开半径为 [${action_value_1.toInt()}] 的领域，，持续施放动作(${action_detail_1 % 10})$time"
            }
            SkillActionType.CHANGE_ACTION_SPEED_FIELD -> ""
            SkillActionType.CHANGE_UB_TIME -> ""
            SkillActionType.LOOP_TRIGGER -> {
                when (action_detail_2) {
                    2 -> "条件：[${action_value_4.toInt()}] 秒内受到伤害时，以 ${
                        getValueText(
                            1,
                            action_value_1,
                            action_value_2,
                            0.0, "%"
                        )
                    } 的概率使用动作(${action_detail_2 % 10})"
                    else -> "?"
                }

            }
            SkillActionType.IF_TARGETED -> ""
            SkillActionType.WAVE_START -> {
                "战斗开始 [${action_value_1}] 秒后入场"
            }
            SkillActionType.SKILL_COUNT -> {
                "[计数器 ${action_detail_1}] 增加 [${action_value_1.toInt()}]"
            }
            SkillActionType.RATE_DAMAGE -> {
                val value = getValueText(1, action_value_1, action_value_2, percent = "%")
                when (action_detail_1) {
                    1 -> "对${getTarget()}造成最大HP $value 伤害"
                    2 -> "对${getTarget()}造成剩余HP $value 伤害"
                    3 -> "对${getTarget()}造成原本的最大HP $value 伤害"
                    else -> "?"
                }
            }
            SkillActionType.UPPER_LIMIT_ATTACK -> {
                "对低等级的玩家造成的伤害将被减轻"
            }
            SkillActionType.HOT -> {
                val type = when (action_detail_2) {
                    1 -> "HP"
                    2 -> "TP"
                    else -> "?"
                }
                val value = getValueText(1, action_value_1, action_value_2, action_value_3)
                val time = getTimeText(action_value_5, action_value_6)
                if (type != "") {
                    "每秒回复${getTarget()}的 $type ${value}${time}"
                } else {
                    ""
                }
            }
            SkillActionType.DISPEL -> {
                val value = getValueText(1, action_value_1, action_value_2, 0.0, "%")
                val type = when (action_detail_1) {
                    1 -> "BUFF"
                    2 -> "DEBUFF"
                    else -> "?"
                }
                if (type != "") {
                    "$value 的概率使${getTarget()}的${type}全部解除"
                } else {
                    ""
                }
            }
            SkillActionType.CHANNEL -> {
                val time = getTimeText(action_value_4, action_value_5)
                val percent = if (action_value_1.toInt() == 1) "" else "%"
                val value = getValueText(2, action_value_2, action_value_3, 0.0, percent)
                val aura = getAura(action_detail_1)
                "${getTarget()}${aura} $value ${time}，受到 [${action_detail_3}] 次伤害时被中断"
            }
            SkillActionType.CHANGE_WIDTH -> {
                "将模型的宽度变为[${action_value_1}]"
            }
            SkillActionType.IF_HAS_FIELD -> {
                if (action_detail_2 != 0 && action_detail_3 != 0) {
                    "条件：特定的领域效果存在时使用动作(${action_detail_2 % 10})，否则使用动作(${action_detail_3 % 10})"
                } else if (action_detail_2 != 0) {
                    "条件：特定的领域效果存在时使用动作(${action_detail_2 % 10})"
                } else {
                    ""
                }
            }
            SkillActionType.STEALTH -> {
                "进入潜伏状态${getTimeText(action_value_1)}"
            }
            SkillActionType.MOVE_PART -> {
                "使部位${action_value_4.toInt()}向前移动 [${-action_value_1.toInt()}] ，随后使其返回原位置"
            }
            SkillActionType.COUNT_BLIND -> {
                val value = getValueText(2, action_value_2, action_value_3)
                val time = getTimeText(action_value_2, action_value_3)
                when (action_value_1.toInt()) {
                    1 -> "使${getTarget()}的物理攻击必定miss$time"
                    2 -> "使${getTarget()}的下 $value 次物理攻击必定miss"
                    else -> "?"
                }
            }
            SkillActionType.COUNT_DOWN -> {
                "对${getTarget()}设置倒计时，[${action_value_1}] 秒后触发动作(${action_detail_1 % 10})"
            }
            SkillActionType.STOP_FIELD -> {
                "解除中第${action_detail_1 / 100 % 10}个技能的动作(${action_detail_1 % 10})展开的领域"
            }
            SkillActionType.INHIBIT_HEAL_ACTION -> {
                "${getTarget()}受到回复效果时，使回复无效并给予其 [${action_value_1} * 回复量] 伤害"
            }
            SkillActionType.ATTACK_SEAL -> {
                val limit = "，叠加上限 [${action_value_1.toInt()}]"
                val time = getTimeText(action_value_3, action_value_4)
                val target = getTarget()
                if (action_detail_1 == 3) {
                    "自身每次攻击${target}，对其增加1层标记${time}$limit"
                } else if (action_detail_1 == 1 && action_detail_3 == 1) {
                    "${target}每次造成伤害时，增加1层标记${time}$limit"
                } else if (action_detail_1 == 4 && action_detail_3 == 1) {
                    "${target}每次造成暴击时，增加1层标记${time}$limit"
                } else {
                    ""
                }
            }
            SkillActionType.FEAR -> {
                val value = getValueText(3, action_value_3, action_value_4, 0.0, "%")
                val time = getTimeText(action_value_1, action_value_2)
                "以 $value 的概率使${getTarget()}进入${toSkillActionType(action_type).desc}状态$time"
            }
            SkillActionType.AWE -> {
                val value = getValueText(1, action_value_1, action_value_2, 0.0, "%")
                val time = getTimeText(action_value_3, action_value_4)
                when (action_detail_1) {
                    0 -> "${getTarget()}的UB对任意目标造成伤害或直接回复时，使其效果值降低 [$value]$time"
                    1 -> "${getTarget()}的UB或技能对任意目标造成伤害或直接回复时，使其效果值降低 [$value]$time"
                    else -> "?"
                }

            }
            SkillActionType.LOOP -> {
                val successClause = if (action_detail_2 != 0)
                    "持续时间结束后，使用动作(${action_detail_2 % 10})。"
                else
                    ""
                val failureClause = if (action_detail_3 != 0)
                    "效果被中断后，使用动作(${action_detail_3 % 10})。"
                else
                    ""
                val main =
                    "每${action_value_2}秒使用 1 次动作(${action_detail_1 % 10})，最长持续 [${action_value_1}] 秒。受到的伤害量超过 [${action_value_3}] 时中断此效果。"
                main + if (successClause != "" && failureClause != "")
                    successClause + failureClause
                else if (successClause != "")
                    successClause
                else if (failureClause != "")
                    failureClause
                else
                    ""
            }
            SkillActionType.TOAD -> {
                val time = getTimeText(action_value_1, action_value_2)
                "使${getTarget()}变身$time"
            }
            SkillActionType.KNIGHT_GUARD -> {
                val value = getValueText(2, action_value_2, action_value_3, action_value_4)
                val time = getTimeText(action_value_6, action_value_7)
                "对${getTarget()}赋予「受到致死伤害时，回复 $value HP」的效果$time"
            }
            SkillActionType.LOG_GUARD -> {
                val time = getTimeText(action_value_3, action_value_4)
                "为${getTarget()}展开护盾，在一次行动中受到的伤害超过 [${action_value_5.toInt()}] 时，伤害值将会被衰减$time"
            }
            SkillActionType.HIT_COUNT -> {
                val limit = if (action_value_5 > 0) {
                    "（上限为 [${action_value_5.toInt()}] 次）"
                } else {
                    ""
                }
                val time = getTimeText(action_value_3, action_value_4)
                when (action_detail_1) {
                    3 -> "每造成[$action_value_1] 次伤害时，使用动作(${action_detail_2 % 10}) $limit$time"
                    else -> "?"
                }
            }
            SkillActionType.EX -> {
                val name = when (action_detail_1) {
                    1 -> "HP"
                    2 -> "物理攻击力"
                    3 -> "物理防御力"
                    4 -> "魔法攻击力"
                    5 -> "魔法防御力"
                    else -> "?"
                }

                "自身的${name}提升 ${getValueText(2, action_value_2, action_value_3)}"
            }
            SkillActionType.CHANGE_TP_RATIO -> {
                "包括通过受伤获得的 TP奖励值在内，使${getTarget()}获得的所有 TP回复效果值变为 [原本的值 * ${action_value_1}]"
            }
            SkillActionType.IGNOR_TAUNT -> {
                "攻击${getTarget()}时，无视其他目标的挑衅效果"
            }
            else -> "?"
        }

        //是否显示系数判断
        val showCoe = when (toSkillActionType(action_type)) {
            SkillActionType.ADDITIVE, SkillActionType.MULTIPLE, SkillActionType.DIVIDE -> true
            else -> false
        }
//        return SkillActionText(
//            tag,
//            "${action_id} \n (${action_id % 10})$action \n$formatDesc",
//            summonUnitId,
//            showCoe,
//            level,
//            atk
//        )
        return SkillActionText(
            tag,
            "(${action_id % 10}) $formatDesc",
            summonUnitId,
            showCoe,
            level,
            atk
        )
    }


    /**
     * , 持续 [] <> 秒
     */
    private fun getTimeText(v1: Double, v2: Double = 0.0): String {
        return if (v2 > 0)
            "，持续 [${v1 + v2 * level}] <$v1 + $v2 * 技能等级> 秒"
        else
            "，持续 [$v1] 秒"
    }

    private fun getValueText(
        index: Int,
        v1: Double,
        v2: Double,
        v3: Double = 0.0,
        percent: String = ""
    ): String {
        return if (v3.int == 0) {
            if (v1.int == 0 && v2.int != 0) {
                "[${(v2 * level).int}$percent] <{{${index + 1}}$v2 * 技能等级>"
            } else if (v1.int != 0 && v2.int == 0) {
                "[${v1}$percent]"
            } else if (v1.int != 0 && v2.int != 0) {
                "[${(v1 + v2 * level).int}$percent] <{${index}}$v1 + {${index + 1}}$v2 * 技能等级>"
            } else {
                "?"
            }
        } else {
            if (v1.int == 0 && v2.int != 0) {
                "[${(v2 + v3 * atk).int}$percent] <{${index + 1}}$v2 + {${index + 2}}$v3 * 攻击力>"
            } else if (v1.int == 0 && v2.int == 0) {
                "[${(v3 * atk).int}$percent] <{${index + 2}}$v3 * 攻击力>"
            } else if (v1.int != 0 && v2.int != 0) {
                "[${(v1 + v2 * level + v3 * atk).int}$percent] <{${index}}$v1 + {${index + 1}}$v2 * 技能等级 + {${index + 2}}$v3 * 攻击力>"
            } else {
                "?"
            }
        }
    }

    private fun getAura(v: Int): String {
        val action = if (v == 1) {
            "HP最大值"
        } else {
            when (v / 10) {
                1 -> "物理攻击力"
                2 -> "物理防御力"
                3 -> "魔法攻击力"
                4 -> "魔法防御力"
                5 -> "回避"
                6 -> "物理暴击"
                7 -> "魔法暴击"
                8 -> "TP上升"
                9 -> "HP吸收"
                10 -> "速度"
                11 -> "物理暴击伤害"
                12 -> "魔法暴击伤害"
                13 -> "命中"
                14 -> "受到的暴击伤害"
                else -> "?"
            }
        }
        var type = if (v % 10 == 0) "提升" else "减少"
        if (v / 10 == 14) {
            type = "百分比" + if (v % 10 == 0) "减少" else "提升"
        }
        return action + type
    }

    /**
     * 护盾类型
     */
    private fun getBarrierTtpe(v1: Int): String {
        return when (v1) {
            1 -> "无效物理伤害的护盾"
            2 -> "无效魔法伤害的护盾"
            3 -> "吸收物理伤害的护盾"
            4 -> "吸收魔法伤害的护盾"
            5 -> "无效所有伤害的护盾"
            6 -> "吸收所有伤害的护盾"
            else -> "?"
        }
    }
}

/**
 * 简化技能效果数据
 */
data class SkillActionText(
    val tag: String,
    var action: String,
    val summonUnitId: Int,
    val showCoe: Boolean,
    val level: Int,
    val atk: Int
)
