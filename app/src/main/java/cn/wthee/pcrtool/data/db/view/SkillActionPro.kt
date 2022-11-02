package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.data.enums.SkillActionType
import cn.wthee.pcrtool.data.enums.getAilment
import cn.wthee.pcrtool.data.enums.toSkillActionType
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getZhNumberText
import cn.wthee.pcrtool.utils.int
import kotlin.math.abs


/**
 * 技能效果
 */
@Suppress("RegExpRedundantEscape")
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
        0
    )

    /**
     * 技能目标分配
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
        0, 1, 99 -> ""
        else -> "（$target_count 名）"
    }

    /**
     * 作用范围
     */
    private fun getTargetRange() = when (target_range) {
        in 1 until 2160 -> "范围($target_range)内"
        else -> ""
    }

    /**
     * 目标类型
     */
    private fun getTargetType() = when (target_type) {
        0, 1, 3 -> ""
        2, 8 -> "随机的"
        4 -> "最远的"
        5, 25 -> "HP最低的"
        6, 26 -> "HP最高的"
        7 -> "自身"
        9 -> "最后方的"
        10 -> "最前方的"
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
        35 -> "剩余HP最高的"
        36 -> "剩余HP最低的"
        38 -> "攻击力（物理攻击力或魔法攻击力）最高的"
        39 -> "攻击力（物理攻击力或魔法攻击力）最低的"
        40 -> ""
        41 -> ""
        42 -> "多目标"
        43 -> "物理攻击力最高（自身除外）的"
        else -> UNKNOWN
    }

    /**
     * 获取目标具体描述
     */
    private fun getTarget(): String {
        val target = if (dependId != 0) {
            "受到动作(${dependId % 10})影响的"
        } else {
            ""
        } + getTargetType() + getTargetNumber() + getTargetRange() + getTargetAssignment() + getTargetCount()
        return target.replace("己方自身", "自身")
            .replace("自身己方", "自身")
            .replace("自身全体", "自身")
            .replace("自身敌人", "自身")
    }


    /**
     * 获取技能效果
     *
     * 判断逻辑参考  MalitsPlus [https://github.com/MalitsPlus]
     */
    fun getActionDesc(): SkillActionText {
        //设置状态标签
        val p = getAilment(action_type)
        if (p.isNotEmpty()) {
            tag = p
        }

        var summonUnitId = 0
        val status = when (action_detail_1) {
            100 -> "无法行动"
            101 -> "加速状态"
            200 -> "失明"
            300 -> "魅惑状态"
            400 -> "挑衅状态"
            500 -> "烧伤状态"
            501 -> "诅咒状态"
            502 -> "中毒状态"
            503 -> "猛毒状态"
            504 -> "咒术状态"
            511 -> "诅咒或咒术状态"
            512 -> "中毒或猛毒状态"
            710 -> "BREAK 状态"
            1400 -> "变身状态"
            1600 -> "恐慌状态"
            1601 -> "隐匿状态"
            1700 -> "魔法防御减少状态"
            721, 6107 -> "龙眼状态"
            1800 -> "多目标状态"
            1900 -> "护盾展开"
            else -> UNKNOWN
        }

        val formatDesc = when (toSkillActionType(action_type)) {
            // 1：造成伤害
            SkillActionType.DAMAGE -> {
                val atkType = when (action_detail_1) {
                    1 -> "物理"
                    2 -> "魔法"
                    3 -> "必定命中的物理"
                    else -> UNKNOWN
                }
                val adaptive = when (action_detail_2) {
                    1 -> "（适应物理/魔法防御中较低的防御）"
                    else -> ""
                }

                //暴伤倍率
                val multipleDamage = if (action_value_6 > 0) {
                    val multiple = if (action_value_6 > 1) {
                        "[${action_value_6 * 2}]"
                    } else {
                        "[2]"
                    }
                    "；暴击时，造成 {6}$multiple 倍伤害 "
                } else {
                    ""
                }

                //必定暴击
                val mustCritical = if (action_value_5.int == 1) "；必定暴击" else ""

                val value = getValueText(1, action_value_1, action_value_2, action_value_3)

                "对${getTarget()}造成 $value 的${atkType}伤害${adaptive}${multipleDamage}${mustCritical}"
            }
            // 2：位移
            SkillActionType.MOVE -> {

                val directionText = if (action_value_1 > 0) "向前" else "向后"
                val positionText = if (action_value_1 > 0) "前方" else "后方"
                val moveText = "移动至${getTarget()}$positionText[${abs(action_value_1)}]"
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
                    else -> UNKNOWN
                }

            }
            // 3：改变对方位置
            SkillActionType.CHANGE_ENEMY_POSITION -> {
                when (action_detail_1) {
                    1, 9 -> {
                        tag = "击飞"
                        "${tag}${getTarget()}，高度[${(abs(action_value_1)).int}]"
                    }
                    3, 6 -> {
                        tag = if (action_value_1 > 0) "击退" else "拉近"
                        "${tag}${getTarget()}，距离[${(abs(action_value_1)).int}]"
                    }
                    8 -> {
                        tag = "拉近"
                        "将${getTarget()}拉到身前 [${action_value_1.int}]"
                    }
                    else -> UNKNOWN
                }
            }
            // 4：回复 HP
            SkillActionType.HEAL -> {
                val value = getValueText(2, action_value_2, action_value_3, action_value_4)
                "使${getTarget()}HP回复 $value"
            }
            // 5：回复 HP
            SkillActionType.CURE -> UNKNOWN
            // 6：护盾
            SkillActionType.BARRIER -> {
                val value = getValueText(1, action_value_1, action_value_2)
                val time = getTimeText(3, action_value_3, action_value_4)
                val type = getBarrierTtpe(action_detail_1)
                if (type != UNKNOWN) {
                    "对${getTarget()}展开${type} ${value}${time}"
                } else {
                    type
                }
            }
            // 7：指定攻击对象
            SkillActionType.CHOOSE_ENEMY -> {
                "锁定${getTarget()}"
            }
            // 8：行动速度变更、83：可叠加行动速度变更
            SkillActionType.CHANGE_ACTION_SPEED, SkillActionType.SUPERIMPOSE_CHANGE_ACTION_SPEED -> {
                //判断异常状态
                tag = when (action_detail_1) {
                    1 -> "减速"
                    2 -> "加速"
                    3 -> "麻痹"
                    4 -> "冻结"
                    5 -> "束缚"
                    6 -> "睡眠"
                    7, 14 -> "眩晕"
                    8 -> "石化"
                    9 -> "拘留"
                    10 -> "昏迷"
                    11 -> "时间停止"
                    13 -> "结晶"
                    else -> UNKNOWN
                }
                val value = getValueText(1, action_value_1, action_value_2)
                val time = getTimeText(3, action_value_3, action_value_4)
                val main = when (action_detail_1) {
                    1, 2 -> {
                        val descText =
                            if (action_type == SkillActionType.SUPERIMPOSE_CHANGE_ACTION_SPEED.type) {
                                tag += "(额外)"
                                "速度额外增加初始值的 $value 倍"
                            } else {
                                "速度变更为初始值的 $value 倍"
                            }
                        "${tag}${getTarget()}，$descText$time"
                    }
                    else -> "使${getTarget()}进入${tag}状态$time"
                }
                if (action_detail_2 == 1) {
                    "$main，本效果将会在受到伤害时解除"
                } else {
                    main
                }
            }
            // 9：持续伤害
            SkillActionType.DOT -> {
                tag = when (action_detail_1) {
                    0 -> "拘留（造成伤害）"
                    1, 7 -> "中毒"
                    2 -> "烧伤"
                    3, 8 -> "诅咒"
                    4 -> "猛毒"
                    5 -> "咒术"
                    else -> UNKNOWN
                }
                val value = getValueText(1, action_value_1, action_value_2)
                val time = getTimeText(3, action_value_3, action_value_4)
                "使${getTarget()}进入${tag}状态，每秒造成伤害 $value${
                    if (action_detail_1 == 5) {
                        "，伤害每秒增加基础数值的 [${action_value_5}%]"
                    } else {
                        ""
                    }
                }$time"

            }
            // 10：buff/debuff
            SkillActionType.AURA -> {
                tag = if (action_detail_1 % 10 == 0) "增益" else "减益"
                val value = getValueText(2, action_value_2, action_value_3, percent = getPercent())
                val aura = getAura(action_detail_1, value)
                val time = getTimeText(4, action_value_4, action_value_5)
                if (action_detail_2 == 2) {
                    "BREAK 期间，${getTarget()}${aura}"
                } else {
                    "${getTarget()}${aura}$time"
                }

            }
            // 11：魅惑/混乱
            SkillActionType.CHARM -> {
                tag = when (action_detail_1) {
                    0 -> "魅惑"
                    1 -> "混乱"
                    else -> UNKNOWN
                }
                val time = getTimeText(1, action_value_1, action_value_2)
                val chance =
                    if (action_value_3 == 100.toDouble()) "成功率 [100%] " else "成功率 [${(1 + action_value_3 * level).int}%] <1 + $action_value_3 * 技能等级> "
                "${tag}${getTarget()}${time}，$chance"
            }
            // 12：黑暗
            SkillActionType.BLIND -> {
                val chance = getValueText(3, action_value_3, action_value_4, 0.0, "%")
                val time = getTimeText(1, action_value_1, action_value_2)
                "以 $chance 的概率使${getTarget()}进入${toSkillActionType(action_type).desc}状态${time}；对象进行物理攻击时有 [${100 - action_detail_1}%] 的概率被回避"
            }
            // 13：沉默
            SkillActionType.SILENCE -> {
                val chance = getValueText(3, action_value_3, action_value_4, 0.0, "%")
                val time = getTimeText(1, action_value_1, action_value_2)
                "以 $chance 的概率使${getTarget()}进入${toSkillActionType(action_type).desc}状态$time"

            }
            // 14：行动模式变更
            SkillActionType.CHANGE_MODE -> {
                when (action_detail_1) {
                    1 -> "技能循环改变${getTimeText(1, action_value_1)}"
                    2 -> "技能循环改变，每秒降低 [${action_value_1}] TP"
                    3 -> "效果结束后，切换回原技能循环"
                    else -> UNKNOWN
                }
            }
            // 15：召唤
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
                        "在${getTarget()}，召唤友方单位"
                    }
                }
            }
            // 16：TP 相关
            SkillActionType.CHANGE_TP -> {
                val value = getValueText(1, action_value_1, action_value_2)
                tag = when (action_detail_1) {
                    1 -> "TP回复"
                    else -> "TP减少"
                }
                "${getTarget()}${tag} $value"
            }
            // 17：触发条件
            SkillActionType.TRIGGER -> {
                val expr = when (action_detail_1) {
                    2 -> "受到伤害时 [${action_value_1.toInt()}%] 概率"
                    3 -> "HP [${action_value_3.toInt()}%] 以下"
                    4 -> "死亡时 [${action_value_1.toInt()}%] 概率"
                    5 -> "暴击时 [${action_value_1.toInt()}%] 概率"
                    7 -> "战斗剩余时间 [${action_value_3.toInt()}] 秒以下"
                    8 -> "隐身时 [${action_value_1.toInt()}%] 概率"
                    9 -> "BREAK 时 [${action_value_1.toInt()}%] 的概率${getTimeText(3, action_value_3)}"
                    10 -> "受到持续伤害时 [${action_value_1.toInt()}%] 概率"
                    11 -> "所有部位 BREAK"
                    else -> UNKNOWN
                }
                "条件：$expr"
            }
            // 18：蓄力、19：伤害充能
            SkillActionType.CHARGE, SkillActionType.DAMAGE_CHARGE -> {
                val value = getValueText(1, action_value_1, action_value_2)
                "蓄力 [${action_value_3}] 秒，动作(${action_detail_2 % 10})效果增加 $value * 蓄力中受到的伤害"
            }
            // 20：挑衅
            SkillActionType.TAUNT -> {
                "使${getTarget()}进入${toSkillActionType(action_type).desc}状态${
                    getTimeText(1, action_value_1, action_value_2)
                }"
            }
            // 21：回避
            SkillActionType.INVINCIBLE -> {
                tag = when (action_detail_1) {
                    1 -> "无敌"
                    2 -> "回避物理攻击"
                    3 -> "回避所有攻击"
                    else -> UNKNOWN
                }
                if (action_value_1 > 0) {
                    "${tag}${getTimeText(1, action_value_1, action_value_2)}"
                } else {
                    tag
                }
            }
            // 22：改变模式
            SkillActionType.CHANGE_PATTERN -> {
                when (action_detail_1) {
                    1 -> if (action_value_1 > 0) {
                        "技能循环改变${getTimeText(1, action_value_1)}"
                    } else {
                        "技能循环改变"
                    }
                    2 -> "技能动画改变${getTimeText(1, action_value_1)}"
                    else -> UNKNOWN
                }
            }
            // 23：判定对象状态
            SkillActionType.IF_STATUS -> {
                var trueClause = UNKNOWN
                var falseClause = UNKNOWN
                if (action_detail_2 != 0) {
                    trueClause = if (status != UNKNOWN) {
                        "当${getTarget()}在[${status}]时，使用动作(${action_detail_2 % 100})"
                    } else {
                        when (action_detail_1) {
                            in 600..699, 710 -> {
                                "${getTarget()}持有标记时，使用动作(${action_detail_2 % 100})"
                            }
                            700 -> {
                                "${getTarget()}是单独时，使用 [${action_detail_2 % 100}]"
                            }
                            in 901..999 -> {
                                "${getTarget()}的HP在 [${action_detail_1 - 900}%] 以下时，使用动作(${action_detail_2 % 100})"
                            }
                            1300 -> {
                                "目标是${getTarget()}时，使用动作(${action_detail_3 % 10})"
                            }
                            else -> UNKNOWN
                        }
                    }
                }
                if (action_detail_3 != 0) {
                    falseClause = if (status != UNKNOWN) {
                        "当${getTarget()}不在[${status}]时，使用动作(${action_detail_3 % 100})"
                    } else {
                        when (action_detail_1) {
                            in 600..699, 710 -> {
                                "${getTarget()}未持有标记时，使用动作(${action_detail_3 % 100})"
                            }
                            700 -> {
                                "${getTarget()}不是单独时，使用 [${action_detail_3 % 100}]"
                            }
                            in 901..999 -> {
                                "${getTarget()}的HP在 [${action_detail_1 - 900}%] 及以上时，使用动作(${action_detail_3 % 100})"
                            }
                            1300 -> {
                                "目标不是${getTarget()}时，使用动作(${action_detail_2 % 10})"
                            }
                            else -> UNKNOWN
                        }
                    }
                }
                //条件
                if (action_detail_1 in 0..99) {
                    when {
                        action_detail_2 != 0 && action_detail_3 != 0 -> {
                            "随机：[${action_detail_1}%] 的概率使用动作(${action_detail_2 % 10})，否则使用动作(${action_detail_3 % 10})"
                        }
                        action_detail_2 != 0 -> {
                            "随机：[${action_detail_1}%] 的概率使用动作(${action_detail_2 % 10})"
                        }
                        action_detail_3 != 0 -> {
                            "随机：[${100 - action_detail_1}%] 的概率使用动作(${action_detail_2 % 10})"
                        }
                        else -> UNKNOWN
                    }
                } else {
                    when {
                        trueClause != UNKNOWN && falseClause != UNKNOWN -> "条件：${trueClause}；${falseClause}"
                        trueClause != UNKNOWN -> "条件：${trueClause}"
                        falseClause != UNKNOWN -> "条件：${falseClause}"
                        else -> UNKNOWN
                    }
                }
            }
            // 24：复活
            SkillActionType.REVIVAL -> {
                "复活${getTarget()}，并回复其 [${(action_value_2 * 100).int}]HP"
            }
            // 25：连续攻击
            SkillActionType.CONTINUOUS_ATTACK -> UNKNOWN
            // 26：系数提升
            SkillActionType.ADDITIVE, SkillActionType.MULTIPLE, SkillActionType.DIVIDE -> {
                val type = when (action_value_1.toInt()) {
                    7 -> "物理攻击力"
                    8 -> "魔法攻击力"
                    9 -> "物理防御力"
                    10 -> "魔法防御力"
                    else -> UNKNOWN
                }
                val changeType = when (toSkillActionType(action_type)) {
                    SkillActionType.ADDITIVE -> "增加"
                    SkillActionType.MULTIPLE -> "乘以"
                    SkillActionType.DIVIDE -> "除以"
                    else -> UNKNOWN
                }
                val commonExpr = getValueText(2, action_value_2, action_value_3, hideIndex = true)
                val commonDesc =
                    "动作(${action_detail_1 % 10})的数值{${action_detail_2}}$changeType $commonExpr"

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
                        "动作(${action_detail_1 % 10})的数值{${action_detail_2}}$changeType$expr * [击杀数量]"
                    }
                    0 -> "$commonDesc * [剩余的HP]"
                    1 -> "$commonDesc * [损失的HP]"
                    4 -> "$commonDesc * [目标数量]"
                    5 -> "$commonDesc * [受到伤害的目标数量]"
                    6 -> "$commonDesc * [造成的伤害]"
                    12 -> "$commonDesc * [后方${getTarget()}数量]"
                    13 -> "$commonDesc * [损失的HP比例]"
                    102 -> "$commonDesc * [小眼球数量]"
                    in 200 until 300 -> "$commonDesc * [标记层数]"
                    in 7..10 -> "$commonDesc * [${getTarget()}的$type]"
                    in 20 until 30 -> "$commonDesc * [标记层数]"
                    in 2112 until 2200 -> "$commonDesc * [标记层数]"
                    else -> UNKNOWN
                }
                //上限判断
                if (action_value_4.toInt() != 0 && action_value_5.toInt() != 0) {
                    val limitValue = getValueText(4, action_value_4, action_value_5)
                    val limit = "；叠加上限 $limitValue"
                    additive + limit
                } else {
                    additive
                }
            }
            // 28：特殊条件
            SkillActionType.IF_SP_STATUS -> {
                var trueClause = UNKNOWN
                var falseClause = UNKNOWN
                if (action_detail_2 != 0 || (action_detail_2 == 0 && action_detail_3 == 0)) {
                    trueClause =
                        when (action_detail_1) {
                            in 0..99 -> {
                                "以 [$action_detail_1%] 的概率使用动作(${action_detail_2 % 10})"
                            }
                            599 -> {
                                "${getTarget()}身上有持续伤害时，使用动作(${action_detail_2 % 10})"
                            }
                            in 600..699 -> {
                                "${getTarget()}的标记层数在 [${action_value_3.toInt()}] 及以上时，使用动作(${action_detail_2 % 10})"
                            }
                            700 -> {
                                "${getTarget()}是单独时，使用 [${action_detail_2 % 10}]"
                            }
                            in 701..709 -> {
                                "隐身状态的单位除外，${getTarget()}的数量是 [${action_detail_1 - 700}] 时，使用动作(${action_detail_2 % 10})"
                            }
                            720 -> {
                                "隐身状态的单位除外，${getTarget()}中存在单位时，使用动作(${action_detail_2 % 10})"
                            }
                            in 901..999 -> {
                                "${getTarget()}的HP在 [${action_detail_1 - 900}%] 以下时，使用动作(${action_detail_2 % 10})"
                            }
                            1000 -> {
                                "上一个动作击杀了单位时，使用动作(${action_detail_2 % 10})"
                            }
                            1001 -> {
                                "技能暴击时，使用动作(${action_detail_2 % 10})"
                            }
                            in 1200..1299 -> {
                                "标记层数在 [${action_detail_1 % 10}] 及以上时，使用动作(${action_detail_2 % 10})"
                            }
                            in 6112..6200 -> {
                                "标记层数达到 [${action_value_3.int}] 时，使用动作(${action_detail_2 % 10})"
                            }
                            else -> if (status != UNKNOWN) {
                                "当${getTarget()}在[${status}]时，使用动作(${action_detail_2 % 100})"
                            } else {
                                UNKNOWN
                            }
                        }
                }

                if (action_detail_3 != 0) {
                    falseClause =
                        when (action_detail_1) {
                            in 0..99 -> {
                                "以 [${100 - action_detail_1}%] 的概率使用动作(${action_detail_3 % 10})"
                            }
                            599 -> {
                                "${getTarget()}身上没有持续伤害时，使用动作(${action_detail_3 % 10})"
                            }
                            in 600..699 -> {
                                "${getTarget()}的标记层数小于 [${action_value_3.toInt()}] 时，使用动作(${action_detail_3 % 10})"
                            }
                            700 -> {
                                "${getTarget()}是不是单独时，使用 [${action_detail_3 % 10}]"
                            }
                            in 701..709 -> {
                                "隐身状态的单位除外，${getTarget()}的数量不是 [${action_detail_1 - 700}] 时，使用动作(${action_detail_3 % 10})"
                            }
                            720 -> {
                                "隐身状态的单位除外，${getTarget()}中不存在单位时，使用动作(${action_detail_3 % 10})"
                            }
                            in 901..999 -> {
                                "${getTarget()}的HP在 [${action_detail_1 - 900}%] 及以上时，使用动作(${action_detail_3 % 10})"
                            }
                            1000 -> {
                                "上一个动作未击杀单位时，使用动作(${action_detail_3 % 10})"
                            }
                            1001 -> {
                                "技能未暴击时，使用动作(${action_detail_3 % 10})"
                            }
                            in 1200..1299 -> {
                                "标记层数小于 [${action_detail_1 % 10}] 时，使用动作(${action_detail_3 % 10})"
                            }
                            in 6112..6200 -> {
                                "标记层数小于 [${action_value_3.int}] 时，使用动作(${action_detail_3 % 10})"
                            }
                            else -> if (status != UNKNOWN) {
                                "当${getTarget()}不在[${status}]时，使用动作(${action_detail_3 % 100})"
                            } else {
                                UNKNOWN
                            }
                        }
                }

                //条件
                when {
                    trueClause != UNKNOWN && falseClause != UNKNOWN -> {
                        "条件：${trueClause}；${falseClause}"
                    }
                    trueClause != UNKNOWN -> "条件：${trueClause}"
                    falseClause != UNKNOWN -> "条件：${falseClause}"
                    else -> UNKNOWN
                }
            }
            // 29：无法使用 UB
            SkillActionType.NO_UB -> "无 UB 技能"
            // 30：立即死亡
            SkillActionType.KILL_ME -> "${getTarget()}死亡"
            SkillActionType.CONTINUOUS_ATTACK_NEARBY -> UNKNOWN
            // 32：HP吸收
            SkillActionType.LIFE_STEAL -> {
                val value = getValueText(1, action_value_1, action_value_2)
                "为${getTarget()}的下 [${action_value_3.toInt()}] 次攻击附加 ${toSkillActionType(action_type).desc} $value 的效果"
            }
            // 33：反伤
            SkillActionType.STRIKE_BACK -> {
                val value = getValueText(1, action_value_1, action_value_2)
                val type = getBarrierTtpe(action_detail_1)
                val shieldText = "对${getTarget()}展开${type} $value"

                when (action_detail_1) {
                    1 -> "${shieldText}，受到物理伤害时反弹 [${action_value_3}] 倍伤害"
                    2 -> "${shieldText}，受到魔法伤害时反弹 [${action_value_3}] 倍伤害"
                    3 -> "${shieldText}，受到物理伤害时反弹 [${action_value_3}] 倍伤害，并回复HP"
                    4 -> "${shieldText}，受到魔法伤害时反弹 [${action_value_3}] 倍伤害，并回复HP"
                    5 -> "${shieldText}，受到伤害时反弹 [${action_value_3}] 倍伤害"
                    6 -> "${shieldText}，受到伤害时反弹 [${action_value_3}] 倍伤害，并回复HP"
                    else -> UNKNOWN
                }
            }
            // 34：伤害递增
            SkillActionType.ACCUMULATIVE_DAMAGE -> {
                val value = getValueText(2, action_value_2, action_value_3)
                "每次攻击当前的目标，将会追加伤害 $value , 叠加上限 [${(action_value_4).int}]"
            }
            // 35：特殊标记
            SkillActionType.SEAL -> {
                if (action_value_4.toInt() > 0) {
                    val time = getTimeText(3, action_value_3)
                    "对${getTarget()}追加 [${action_value_4.toInt()}] 层标记${time}，叠加上限 [${action_value_1.toInt()}]"
                } else {
                    "${getTarget()}减少 [${abs(action_value_4).toInt()}] 层标记"
                }
            }
            // 36：攻击领域展开
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

                "展开领域 [${action_value_7.toInt()}] ${value}${
                    getTimeText(5, action_value_5, action_value_6)
                }"
            }
            // 37：治疗领域展开
            SkillActionType.HEAL_FIELD -> {
                val value =
                    "，每秒回复 ${getValueText(1, action_value_1, action_value_2, action_value_3)}HP"

                "展开领域 [${action_value_7.toInt()}] ${value}${
                    getTimeText(5, action_value_5, action_value_6)
                }"
            }
            // 38：buff/debuff领域展开
            SkillActionType.AURA_FIELD -> {
                val value = getValueText(1, action_value_1, action_value_2, percent = getPercent())
                val time = getTimeText(3, action_value_3, action_value_4)
                "${getTarget()}展开领域 [${action_value_5.toInt()}] ，${
                    getAura(action_detail_1, value)
                }$time"
            }
            // 39：持续伤害领域展开
            SkillActionType.DOT_FIELD -> {
                val time = getTimeText(1, action_value_1, action_value_2)
                "${getTarget()}展开领域 [${action_value_3.toInt()}] ，持续施放动作(${action_detail_1 % 10})$time"
            }
            SkillActionType.CHANGE_ACTION_SPEED_FIELD -> UNKNOWN
            SkillActionType.CHANGE_UB_TIME -> UNKNOWN
            // 42：触发
            SkillActionType.LOOP_TRIGGER -> {
                when (action_detail_1) {
                    2 -> "[${action_value_4.toInt()}] 秒内受到伤害时，以 ${
                        getValueText(
                            1,
                            action_value_1,
                            action_value_2,
                            0.0, "%"
                        )
                    } 的概率使用动作(${action_detail_2 % 10})"
                    else -> UNKNOWN
                }
            }
            SkillActionType.IF_TARGETED -> UNKNOWN
            // 44：进场等待
            SkillActionType.WAVE_START -> {
                "战斗开始 [${action_value_1}] 秒后入场"
            }
            // 45：已使用技能数相关
            SkillActionType.SKILL_COUNT -> {
                "追加 [1] 层标记，叠加上限 [${action_value_1.toInt()}]"
            }
            // 46：比例伤害
            SkillActionType.RATE_DAMAGE -> {
                val value = getValueText(1, action_value_1, action_value_2, percent = "%")
                when (action_detail_1) {
                    1 -> "对${getTarget()}造成最大HP $value 伤害"
                    2 -> "对${getTarget()}造成剩余HP $value 伤害"
                    3 -> "对${getTarget()}造成原本的最大HP $value 伤害"
                    else -> UNKNOWN
                }
            }
            SkillActionType.UPPER_LIMIT_ATTACK -> {
                "对低等级的玩家造成的伤害将被减轻"
            }
            // 48：持续治疗
            SkillActionType.HOT -> {
                val type = when (action_detail_2) {
                    1 -> "HP"
                    2 -> "TP"
                    else -> UNKNOWN
                }
                val value = getValueText(1, action_value_1, action_value_2, action_value_3)
                val time = getTimeText(5, action_value_5, action_value_6)
                if (type != UNKNOWN) {
                    "每秒回复${getTarget()}的 $type ${value}${time}"
                } else {
                    UNKNOWN
                }
            }
            // 49：移除增益
            SkillActionType.DISPEL -> {
                val value = getValueText(1, action_value_1, action_value_2, 0.0, "%")
                val type = when (action_detail_1) {
                    1 -> "BUFF"
                    2 -> "DEBUFF"
                    10 -> "护盾"
                    else -> UNKNOWN
                }
                if (type != UNKNOWN) {
                    " $value 概率使${getTarget()}的${type}全部移除"
                } else {
                    UNKNOWN
                }
            }
            // 50：持续动作
            SkillActionType.CHANNEL -> {
                val time = getTimeText(4, action_value_4, action_value_5)
                val value = getValueText(2, action_value_2, action_value_3, percent = getPercent())
                val aura = getAura(action_detail_1, value)
                "${getTarget()}${aura}${time}，受到 [${action_detail_3}] 次伤害时中断"
            }
            // 52：改变单位距离
            SkillActionType.CHANGE_WIDTH -> {
                "将模型的宽度变为[${action_value_1}]"
            }
            // 53：特殊状态：领域存在时；如：情姐
            SkillActionType.IF_HAS_FIELD -> {
                if (action_detail_2 != 0 && action_detail_3 != 0) {
                    "条件：特定的领域效果存在时使用动作(${action_detail_2 % 10})，否则使用动作(${action_detail_3 % 10})"
                } else if (action_detail_2 != 0) {
                    "条件：特定的领域效果存在时使用动作(${action_detail_2 % 10})"
                } else {
                    UNKNOWN
                }
            }
            // 54：隐身
            SkillActionType.STEALTH -> {
                "进入隐身状态${getTimeText(1, action_value_1)}"
            }
            // 55：部位移动
            SkillActionType.MOVE_PART -> {
                "使部位${action_value_4.toInt()}向前移动 [${-action_value_1.toInt()}] ，随后使其返回原位置"
            }
            // 56：千里眼
            SkillActionType.COUNT_BLIND -> {
                val value = getValueText(2, action_value_2, action_value_3)
                val time = getTimeText(2, action_value_2, action_value_3)
                when (action_value_1.toInt()) {
                    1 -> "使${getTarget()}的物理攻击必定被回避$time"
                    2 -> "使${getTarget()}的下 $value 次物理攻击必定被回避"
                    else -> UNKNOWN
                }
            }
            // 57：延迟攻击 如：万圣炸弹人的 UB
            SkillActionType.COUNT_DOWN -> {
                "对${getTarget()}设置倒计时，[${action_value_1}] 秒后触发动作(${action_detail_1 % 10})"
            }
            // 58：解除领域 如：晶姐 UB
            SkillActionType.STOP_FIELD -> {
                "解除第${action_detail_1 / 100 % 10}个技能的动作(${action_detail_1 % 10})展开的领域"
            }
            // 59：回复妨碍
            SkillActionType.INHIBIT_HEAL_ACTION -> {
                "${getTarget()}，HP回复效果减少 [${action_value_1 * 100}%]${
                    getTimeText(
                        2,
                        action_value_2
                    )
                }"
            }
            // 60：标记赋予
            SkillActionType.ATTACK_SEAL -> {
                val limit = "，叠加上限 [${action_value_1.toInt()}]"
                val time = getTimeText(3, action_value_3, action_value_4)
                val target = getTarget()
                if (action_detail_1 == 3) {
                    "自身每次攻击${target}，追加 [1] 层标记${time}$limit"
                } else if (action_detail_1 == 1 && action_detail_3 == 1) {
                    "${target}每次造成伤害时，追加 [1] 层标记${time}$limit"
                } else if (action_detail_1 == 4 && action_detail_3 == 1) {
                    "${target}每次造成暴击时，追加 [1] 层标记${time}$limit"
                } else {
                    UNKNOWN
                }
            }
            // 61：恐慌
            SkillActionType.FEAR -> {
                val value = getValueText(3, action_value_3, action_value_4, 0.0, "%")
                val time = getTimeText(1, action_value_1, action_value_2)
                "以 $value 的概率使${getTarget()}进入${toSkillActionType(action_type).desc}状态$time"
            }
            // 62：畏惧
            SkillActionType.AWE -> {
                val value = getValueText(1, action_value_1, action_value_2, 0.0, "%")
                val time = getTimeText(3, action_value_3, action_value_4)
                when (action_detail_1) {
                    0 -> "${getTarget()}的UB对任意目标造成伤害或直接回复时，使其效果值降低 $value$time"
                    1 -> "${getTarget()}的UB或技能对任意目标造成伤害或直接回复时，使其效果值降低 $value$time"
                    else -> UNKNOWN
                }
            }
            // 63: 循环动作
            SkillActionType.LOOP -> {
                val successClause = if (action_detail_2 != 0)
                    "持续时间结束后，使用动作(${action_detail_2 % 10})；"
                else
                    UNKNOWN
                val failureClause = if (action_detail_3 != 0)
                    "效果被中断后，使用动作(${action_detail_3 % 10})；"
                else
                    UNKNOWN
                val main =
                    "每${action_value_2}秒使用 1 次动作(${action_detail_1 % 10})，最长持续 [${action_value_1}] 秒；受到的伤害量超过 [${action_value_3}] 时中断此效果；"
                main + if (successClause != UNKNOWN && failureClause != UNKNOWN)
                    successClause + failureClause
                else if (successClause != UNKNOWN)
                    successClause
                else if (failureClause != UNKNOWN)
                    failureClause
                else
                    UNKNOWN
            }
            // 69：驯鹿化
            SkillActionType.REINDEER -> {
                val time = getTimeText(1, action_value_1, action_value_2)
                "使${getTarget()}变身$time"
            }
            // 71：特殊状态：公主佩可 UB 后不死BUFF
            SkillActionType.KNIGHT_GUARD -> {
                val value = getValueText(2, action_value_2, action_value_3, action_value_4)
                val time = getTimeText(6, action_value_6, action_value_7)
                "对${getTarget()}赋予「受到致死伤害时，回复 $value HP」的效果$time"
            }
            // 72：伤害减免
            SkillActionType.DAMAGE_REDUCE -> {
                val type = when (action_detail_1) {
                    1 -> "物理"
                    2 -> "魔法"
                    3 -> "物理、魔法"
                    else -> UNKNOWN
                }
                val value = getValueText(1, action_value_1, action_value_2, percent = getPercent())
                val time = getTimeText(3, action_value_3, action_value_4)
                "对${getTarget()}赋予${type}减伤${value}的效果$time"
            }
            // 73：伤害护盾
            SkillActionType.LOG_GUARD -> {
                val time = getTimeText(3, action_value_3, action_value_4)
                "为${getTarget()}展开护盾，在一次行动中受到的伤害超过 [${action_value_5.toInt()}] 时，伤害值将衰减$time"
            }
            // 75：依据攻击次数增伤
            SkillActionType.HIT_COUNT -> {
                val limit = if (action_value_5 > 0) {
                    "，叠加上限 [${action_value_5.toInt()}]"
                } else {
                    ""
                }
                val time = getTimeText(3, action_value_3, action_value_4)
                when (action_detail_1) {
                    3 -> "每造成[$action_value_1] 次伤害时，使用动作(${action_detail_2 % 10}) $limit$time"
                    else -> UNKNOWN
                }
            }
            // 76：HP 回复量减少
            SkillActionType.HEAL_DOWN -> {
                val value = getValueText(1, action_value_1, action_value_2, percent = getPercent())
                val time = getTimeText(3, action_value_3, action_value_4)
                "${getTarget()}治疗量变为原来的 $value 倍$time"
            }
            // 77：被动叠加标记
            SkillActionType.IF_BUFF_SEAL -> {
                val time = getTimeText(3, action_value_3, action_value_4)
                val lifeTime = getTimeText(5, action_value_5, action_value_6)
                val effect = when (action_detail_1) {
                    1 -> "BUFF"
                    2 -> "伤害"
                    else -> UNKNOWN
                }
                "被动效果：每当${getTarget()}受到${effect}时，为自身追加 [${action_detail_2}] 层标记$time，叠加上限 [${action_value_1.int}]。被动效果$lifeTime"
            }
            // 79：行动时，造成伤害
            SkillActionType.ACTION_DOT -> {
                "${getTarget()}行动时，受到 ${
                    getValueText(
                        1,
                        action_value_1,
                        action_value_2,
                    )
                }伤害${getTimeText(3, action_value_3, action_value_4)}"
            }
            // 81：无效目标
            SkillActionType.NO_TARGET -> {
                "${getTarget()}变更为无法被攻击的目标"
            }
            // 90：EX被动
            SkillActionType.EX -> {
                val name = when (action_detail_1) {
                    1 -> "HP"
                    2 -> "物理攻击力"
                    3 -> "物理防御力"
                    4 -> "魔法攻击力"
                    5 -> "魔法防御力"
                    else -> UNKNOWN
                }
                "自身的${name}提升 ${getValueText(2, action_value_2, action_value_3)}"
            }
            // 901：ex装备被动被动 902：45秒
            SkillActionType.EX_EQUIP, SkillActionType.EX_EQUIP_HALF -> {
                "装备技能"
            }
            // 92：改变 TP 获取倍率
            SkillActionType.CHANGE_TP_RATIO -> {
                "使${getTarget()}受击获得的TP变更为初始值的 [${action_value_1}] 倍"
            }
            // 93：无视挑衅
            SkillActionType.IGNOR_TAUNT -> {
                "攻击${getTarget()}时，无视挑衅效果"
            }
            // 94：技能特效
            SkillActionType.SPECIAL_EFFECT -> {
                "${getTarget()}附加技能特效"
            }
            // 95：隐匿
            SkillActionType.HIDE -> {
                "${getTarget()}进入隐匿状态${getTimeText(1, action_value_1, action_value_2)}"
            }
            // 96：范围tp回复
            SkillActionType.TP_FIELD -> {
                val value =
                    "，每秒回复 ${getValueText(1, action_value_1, action_value_2)}TP"

                "展开领域 [${action_value_5.toInt()}] ${value}${
                    getTimeText(3, action_value_3, action_value_4)
                }"
            }
            // 97：受击tp回复
            SkillActionType.TP_HIT -> {
                "受击时减少 [${action_value_3.int}]标记，TP回复 [$action_value_1]。标记叠加上限 [${action_value_4.int}]"
            }
            else -> "${UNKNOWN}目标：${getTarget()}；类型：${action_type}；数值：${
                getValueText(
                    1,
                    action_value_1,
                    action_value_2,
                    percent = getPercent()
                )
            }${getTimeText(3, action_value_3, action_value_4)}"
        }

        //是否显示系数判断
        val showCoe = when (toSkillActionType(action_type)) {
            SkillActionType.ADDITIVE,
            SkillActionType.MULTIPLE,
            SkillActionType.DIVIDE,
            SkillActionType.RATE_DAMAGE -> true
            else -> false
        }
        val skillActionText = SkillActionText(
            action_id,
            tag,
            "(${action_id % 10}) $formatDesc",
            summonUnitId,
            showCoe,
            level,
            atk
        )
        if (BuildConfig.DEBUG) {
            skillActionText.debugText = this.toString()
        }
        return skillActionText
    }

    /**
     * 获取 %
     */
    private fun getPercent() = when (toSkillActionType(action_type)) {
        SkillActionType.AURA, SkillActionType.HEAL_DOWN -> {
            if (action_value_1.int == 2 || action_detail_1 / 10 in setOf(11, 12, 14, 16, 17)) {
                "%"
            } else {
                ""
            }
        }
        SkillActionType.HEAL_FIELD, SkillActionType.AURA_FIELD -> if (action_detail_2 == 2) "%" else ""
        SkillActionType.DAMAGE_REDUCE -> "%"
        else -> ""
    }

    /**
     * 持续时间
     */
    private fun getTimeText(index: Int, v1: Double, v2: Double = 0.0): String {
        return "，持续 ${getValueText(index, v1, v2)} 秒"
    }

    /**
     * 获取数值
     *
     * @param index v1 传的值 action_value_? -> index = ?
     * @param percent 显示 %
     */
    private fun getValueText(
        index: Int,
        v1: Double,
        v2: Double,
        v3: Double = 0.0,
        percent: String = "",
        hideIndex: Boolean = false
    ): String {
        val value = if (v3 == 0.0) {
            if (v1 == 0.0 && v2 != 0.0) {
                "[${(v2 * level).int}$percent] <{${index + 1}}$v2 * 技能等级>"
            } else if (v1 != 0.0 && v2 == 0.0) {
                "{${index}}[${v1}$percent]"
            } else if (v1 != 0.0) {
                "[${(v1 + v2 * level).int}$percent] <{${index}}$v1 + {${index + 1}}$v2 * 技能等级>"
            } else {
                "{$index}$percent"
            }
        } else {
            if (v1 == 0.0 && v2 != 0.0) {
                "[${(v2 + v3 * atk).int}$percent] <{${index + 1}}$v2 + {${index + 2}}$v3 * 攻击力>"
            } else if (v1 == 0.0) {
                "[${(v3 * atk).int}$percent] <{${index + 2}}$v3 * 攻击力>"
            } else if (v2 != 0.0) {
                "[${(v1 + v2 * level + v3 * atk).int}$percent] <{${index}}$v1 + {${index + 1}}$v2 * 技能等级 + {${index + 2}}$v3 * 攻击力>"
            } else {
                "{$index}$percent"
            }
        }
        return if (hideIndex) {
            value.replace(Regex("\\{.*?\\}"), "")
        } else {
            value
        }
    }

    /**
     * 效果
     */
    private fun getAura(v: Int, valueText: String): String {
        val action = if (v == 1) {
            "HP最大值"
        } else {
            when (v % 1000 / 10) {
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
                16 -> "受到的物理伤害"
                17 -> "受到的魔法伤害"
                else -> UNKNOWN
            }
        }
        var type = (if (v % 10 == 0) "提升" else "减少") + " " + valueText
        if (v / 10 == 14 || v / 10 == 16 || v / 10 == 17) {
            type = (if (v % 10 == 0) "减少" else "提升") + " " + valueText
        }
        //固定buff，不受其他效果影响
        if (v > 1000) {
            type += "（固定数值，无法被其他技能效果改变）"
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
            else -> UNKNOWN
        }
    }
}

/**
 * 简化技能效果数据
 */
data class SkillActionText(
    val actionId: Int,
    val tag: String,
    var action: String,
    val summonUnitId: Int,
    val showCoe: Boolean,
    val level: Int,
    val atk: Int
) {
    var debugText = ""
}
