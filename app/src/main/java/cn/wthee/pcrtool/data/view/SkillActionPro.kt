package cn.wthee.pcrtool.data.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.data.enums.SkillActionType
import cn.wthee.pcrtool.data.enums.getAilment
import cn.wthee.pcrtool.data.enums.toSkillActionType
import cn.wthee.pcrtool.data.model.int
import cn.wthee.pcrtool.utils.getZhNumberText
import kotlin.math.abs


/**
 * 技能效果
 */
data class SkillActionPro(
    @PrimaryKey
    @ColumnInfo(name = "lv") val level: Int,
    @ColumnInfo(name = "atk") val atk: Int,
    @ColumnInfo(name = "action_id") val action_id: Int,
    @ColumnInfo(name = "class_id") val class_id: Int,
    @ColumnInfo(name = "action_type") val action_type: Int,
    @ColumnInfo(name = "action_detail_1") val action_detail_1: Int,
    @ColumnInfo(name = "action_detail_2") val action_detail_2: Int,
    @ColumnInfo(name = "action_detail_3") val action_detail_3: Int,
    @ColumnInfo(name = "action_value_1") val action_value_1: Double,
    @ColumnInfo(name = "action_value_2") val action_value_2: Double,
    @ColumnInfo(name = "action_value_3") val action_value_3: Double,
    @ColumnInfo(name = "action_value_4") val action_value_4: Double,
    @ColumnInfo(name = "action_value_5") val action_value_5: Double,
    @ColumnInfo(name = "action_value_6") val action_value_6: Double,
    @ColumnInfo(name = "action_value_7") val action_value_7: Double,
    @ColumnInfo(name = "target_assignment") val target_assignment: Int,
    @ColumnInfo(name = "target_area") val target_area: Int,
    @ColumnInfo(name = "target_range") val target_range: Int,
    @ColumnInfo(name = "target_type") val target_type: Int,
    @ColumnInfo(name = "target_number") val target_number: Int,
    @ColumnInfo(name = "target_count") val target_count: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "level_up_disp") val level_up_disp: String,
    @ColumnInfo(name = "ailment_name") var tag: String
) {

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
        99 -> "(全体)"
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
        return (getTargetType() + getTargetNumber() + getTargetRange() + getTargetAssignment() + getTargetCount())
            .replace("己方自身", "自身")
            .replace("自身己方", "自身")

    }


    /**
     * 获取技能效果
     *
     * 技能效果判断逻辑来源 @author MalitsPlus[https://github.com/MalitsPlus]
     *
     */
    fun getActionDesc(): SkillActionText {
        //设置状态标签
        val p = getAilment(action_type)
        if (p.isNotEmpty()) {
            tag = p
        }

        val desc = when (toSkillActionType(action_type)) {
            SkillActionType.DAMAGE, SkillActionType.HOT -> {
                " [${(action_value_1 + action_value_2 * level + action_value_3 * atk).int}] <$action_value_1 + $action_value_2 * 技能等级 + $action_value_3 * 攻击力> "
            }
            SkillActionType.MOVE -> {
                "移动至最近敌人前 [$action_value_1] " + if (action_value_2 > 0) "，移动速度 [${action_value_2}] " else ""
            }
            SkillActionType.CHANGE_ENEMY_POSITION -> {
                tag = if (action_detail_2 == 0) {
                    "击飞"
                } else {
                    if (action_value_1 > 0) "击退" else "拉近"
                }
                "$tag [${(abs(action_value_4)).int}] "
            }
            SkillActionType.HEAL -> {
                " [${(action_value_2 + action_value_3 * level + action_value_4 * atk).int}] <$action_value_2 + $action_value_3 * 技能等级 + $action_value_4 * 攻击力> "
            }
            SkillActionType.SHIELD -> {
                " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> ，持续 [${action_value_3}] 秒"
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
                    else -> ""
                }
                if (action_value_1 != 0.toDouble()) {
                    "，速度 *  [${action_value_1}] ， 持续 [${action_value_3}] 秒"
                } else {
                    "，持续 [${action_value_3}] 秒"
                }
            }
            SkillActionType.DOT -> {
                tag = when (action_detail_1) {
                    0 -> "拘留（造成伤害）"
                    1 -> "毒"
                    2 -> "烧伤"
                    3, 5 -> "诅咒"
                    4 -> "猛毒"
                    else -> ""
                }
                " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> ，持续 [${action_value_3}] 秒"
            }
            SkillActionType.AURA -> {
                tag = if (target_assignment == 1) "DEBUFF" else "BUFF"
                if (action_value_3 > 0) {
                    " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> "
                } else {
                    " [${(action_value_2).int}] "
                } + "，持续 [${action_value_4}] 秒"
            }
            SkillActionType.CHARM -> {
                tag = when (action_detail_1) {
                    0 -> "魅惑"
                    1 -> "混乱"
                    else -> ""
                }
                "，持续 [${action_value_1}] 秒，" + if (action_value_3 == 100.toDouble()) "成功率 [100] " else "成功率 [${(1 + action_value_3 * level).int}] <1 + $action_value_3 * 技能等级> "
            }
            SkillActionType.BLIND -> {
                "，持续 [${action_value_1}] 秒，" + if (action_value_3 == 100.toDouble()) "成功率 [100] " else "成功率 [${(1 + action_value_3 * level).int}] <1 + $action_value_3 * 技能等级> "
            }
            SkillActionType.CHANGE_MODE -> {
                if (action_value_1 > 0) "每秒降低TP [${(action_value_1).int}] " else ""
            }
            SkillActionType.CHANGE_TP -> {
                if (action_value_2 > 0)
                    " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> "
                else
                    " [${(action_value_1).int}] "
            }
            SkillActionType.TAUNT, SkillActionType.INVINCIBLE -> {
                if (action_value_2 > 0) ", 持续 [${action_value_1 + action_value_2 * level}] <$action_value_1 + $action_value_2 * 技能等级> 秒" else ", 持续 [${action_value_1}] 秒"
            }
            //反伤
            SkillActionType.STRIKE_BACK -> {
                " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> "
            }
            //伤害叠加
            SkillActionType.ACCUMULATIVE_DAMAGE -> {
                " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> , 叠加上限 [${(action_value_4).int}] "
            }
            SkillActionType.ATTACK_FIELD -> {
                " [${(action_value_1 + action_value_2 * level + action_value_3 * atk).int}] <$action_value_1 + $action_value_2 * 技能等级 + $action_value_3 * 攻击力> ，持续 [${action_value_5}] 秒，范围 [${(action_value_7).int}] "
            }
            SkillActionType.HEAL_FIELD,
            SkillActionType.DEBUFF_FIELD,
            SkillActionType.DOT_FIELD -> {
                if (action_value_2 == 0.toDouble()) {
                    "$action_value_1 ，持续 [${action_value_4}] 秒"
                } else {
                    " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> ，持续 [${action_value_3}] 秒"
                } + "，范围 <$action_value_7> ；"
            }
            SkillActionType.CHANNEL -> {
                " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> ，持续 [${action_value_4}] 秒"
            }
            SkillActionType.EX -> if (action_value_3 == 0.toDouble()) {
                " [$action_value_2] "
            } else {
                " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> "
            }
            else -> ""
        }
        //TODO 细化判断，替代默认描述
        var summonUnitId = 0
        val status = when (action_detail_1) {
            100 -> "无法行动"
            200 -> "失明"
            300 -> "魅惑或混乱状态"
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
                val expr =
                    "[${(action_value_1 + action_value_2 * level + action_value_3 * atk).int}] <$action_value_1 + $action_value_2 * 技能等级 + $action_value_3 * 攻击力>"
                "对${getTarget()}造成 $expr 的${atkType}伤害"
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
                "回复HP [${(action_value_2 + action_value_3 * level + action_value_4 * atk).int}] <$action_value_2 + $action_value_3 * 技能等级 + $action_value_4 * 攻击力> "
            }
            SkillActionType.SHIELD -> {
                val expr =
                    "[${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级>"
                val shieldText = "对${getTarget()}展开"
                val suffix = "的护盾${expr}${getTimeText(action_value_3, action_value_4)}"
                when (action_detail_1) {
                    1 -> "${shieldText}承受物理伤害${suffix}"
                    2 -> "${shieldText}承受魔法伤害${suffix}"
                    3 -> "${shieldText}物理伤害无效${suffix}"
                    4 -> "${shieldText}魔法伤害无效${suffix}"
                    5 -> "${shieldText}承受所有伤害${suffix}"
                    6 -> "${shieldText}所有伤害无效${suffix}"
                    else -> ""
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
                    else -> ""
                }
                "${tag}${getTarget()}" + if (action_value_1 != 0.toDouble()) {
                    "，速度 * [${action_value_1}]"
                } else {
                    ""
                } + getTimeText(action_value_3, action_value_4)
            }
            SkillActionType.DOT -> {
                tag = when (action_detail_1) {
                    0 -> "拘留（造成伤害）"
                    1 -> "中毒"
                    2 -> "烧伤"
                    3, 5 -> "诅咒"
                    4 -> "猛毒"
                    else -> ""
                }
                val expr =
                    "[${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级>"
                "${tag}${getTarget()}${expr}${getTimeText(action_value_3, action_value_4)}"
            }
            SkillActionType.AURA -> {
                tag = if (action_detail_1 % 10 == 0) "增益" else "减益"
                val percent = if (action_value_1.int == 1) "" else "%"
                val expr = if (action_value_3 > 0) {
                    "[${(action_value_2 + action_value_3 * level).int}${percent}] <$action_value_2 + $action_value_3 * 技能等级> "
                } else {
                    "[${(action_value_2).int}${percent}]"
                }
                "${getTarget()}${getAura(action_detail_1)}${expr}${
                    getTimeText(
                        action_value_4,
                        action_value_5
                    )
                }"
            }
            SkillActionType.CHARM -> {
                tag = when (action_detail_1) {
                    0 -> "魅惑"
                    1 -> "混乱"
                    else -> ""
                }
                "${tag}${getTarget()}${
                    getTimeText(
                        action_value_1,
                        action_value_2
                    )
                }，" + if (action_value_3 == 100.toDouble()) "成功率 [100] " else "成功率 [${(1 + action_value_3 * level).int}] <1 + $action_value_3 * 技能等级> "
            }
            SkillActionType.BLIND -> {
                "失明${getTarget()}${
                    getTimeText(
                        action_value_1,
                        action_value_2
                    )
                }，" + if (action_value_3 == 100.toDouble()) "成功率 [100] " else "成功率 [${(1 + action_value_3 * level).int}] <1 + $action_value_3 * 技能等级> "
            }
            SkillActionType.SILENCE -> {
                "${toSkillActionType(action_type).desc}${getTarget()}，成功率 [${action_value_3.int}]"
            }
            SkillActionType.CHANGE_MODE -> {
                when (action_detail_1) {
                    1 -> "技能循环改变，持续 [${action_value_1}] 秒"
                    2 -> "技能循环改变，每秒降低TP[${action_value_1}]"
                    3 -> "效果结束后，切换回技能循环"
                    else -> ""
                }
            }
            SkillActionType.SUMMON -> {
                summonUnitId = action_detail_2
                when {
                    action_value_7 > 0 -> {
                        "在${getTarget()}前方 [${action_value_7.int}] 的位置，召唤 [${action_detail_2}] 的召唤物"
                    }
                    action_value_7 < 0 -> {
                        "在${getTarget()}后方 [${abs(action_value_7).int}] 的位置，召唤 [${action_detail_2}] 的召唤物"
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
                    2 -> "受到伤害时 [${action_value_1.int}%] 概率"
                    3 -> "HP [${action_value_3.int}%] 以下"
                    4 -> "死亡时 [${action_value_1.int}%] 概率"
                    5 -> "暴击时 [${action_value_1.int}%] 概率"
                    7 -> "战斗剩余时间 [${action_value_3.int}] 秒以下"
                    8 -> "潜伏时 [${action_value_1.int}%] 概率"
                    9 -> "BREAK 时 [${action_value_1.int}%] 的概率，持续 [${action_value_3.int}] 秒"
                    10 -> "受到持续伤害时 [${action_value_1.int}%] 概率"
                    11 -> "所有部位 BREAK"
                    else -> "未知"
                }
                "条件：$expr"
            }
            SkillActionType.CHARGE, SkillActionType.DAMAGE_CHARGE -> {
                "蓄力 [${action_value_3}] 秒" + when {
                    action_detail_2 > 0 -> {
                        "，下一个动作的效果提高 [${(action_value_1 + action_value_2 * level)}] <$action_value_1 + $action_value_2 * 技能等级> * 蓄力中受到的伤害"
                    }
                    action_value_1 > 0 -> {
                        "，下一个动作的效果提高 [${action_value_1}] * 蓄力中受到的伤害"
                    }
                    else -> {
                        ""
                    }
                }
            }
            SkillActionType.TAUNT -> {
                "${toSkillActionType(action_type)}${getTarget()}${
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
                    else -> ""
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
                    else -> ""
                }
            }
            //fixme 优化描述、判断逻辑
            SkillActionType.IF_FOR_CHILDREN -> {
                var trueClause = ""
                var falseClause = ""
                if (action_detail_2 != 0) {
                    trueClause = if (status != "") {
                        if (action_detail_1 == 502) {
                            "当${getTarget()}在[${status}]的场合，使用 [动作 ${action_detail_3 % 100}]"
                        } else {
                            "当${getTarget()}在[${status}]的场合，使用 [动作 ${action_detail_2 % 100}]"
                        }
                    } else {
                        if ((action_detail_1 in 600..699) || action_detail_1 == 710) {
                            "${getTarget()}持有标记 [ID: ${action_detail_1 - 600}] 的场合，使用 [动作 ${action_detail_2 % 100}]"
                        } else if (action_detail_1 == 700) {
                            "${getTarget()}是单独的场合，使用 [${action_detail_2 % 100}]"
                        } else if (action_detail_1 in 901..999) {
                            "${getTarget()}的HP在 [${action_detail_1 - 900}%] 以下的场合，使用 [动作 ${action_detail_2 % 100}]"
                        } else if (action_detail_1 == 1300) {
                            "${getTarget()}是使用魔法攻击对象的场合，使用 [动作 ${action_detail_3 % 10}]"
                        } else {
                            ""
                        }
                    }
                }
                if (action_detail_3 != 0) {
                    falseClause = if (status != "") {
                        if (action_detail_1 == 502) {
                            "当${getTarget()}在[${status}]的场合，使用 [动作 ${action_detail_2 % 100}]"
                        } else {
                            "当${getTarget()}不在[${status}]的场合，使用 [动作 ${action_detail_3 % 100}]"
                        }
                    } else {
                        if ((action_detail_1 in 600..699) || action_detail_1 == 710) {
                            "${getTarget()}未持有标记 [ID: ${action_detail_1 - 600}] 的场合，使用 [动作 ${action_detail_3 % 100}]"
                        } else if (action_detail_1 == 700) {
                            "${getTarget()}不，使用 [${action_detail_3 % 100}]"
                        } else if (action_detail_1 in 901..999) {
                            "${getTarget()}的HP不在 [${action_detail_1 - 900}%] 以下的场合，使用 [动作 ${action_detail_3 % 100}]"
                        } else if (action_detail_1 == 1300) {
                            "${getTarget()}不是使用魔法攻击对象的场合，使用 [动作 ${action_detail_2 % 10}]"
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
                        "条件分歧：${trueClause}${falseClause}"
                    else if (trueClause != "")
                        "条件分歧：${trueClause}"
                    else if (falseClause != "")
                        "条件分歧：${falseClause}"
                    else
                        ""
                } else if (action_detail_1 in 0..99) {
                    if (action_detail_2 != 0 && action_detail_3 != 0) {
                        "随机事件：[${action_detail_1}%] 的概率使用 [动作 ${action_detail_2 % 10}]，否则使用 [动作 ${action_detail_3 % 10}]"
                    } else if (action_detail_2 != 0) {
                        "随机事件：[${action_detail_1}%] 的概率使用 [动作 ${action_detail_2 % 10}]"
                    } else if (action_detail_3 != 0) {
                        "随机事件：[${100 - action_detail_1}%] 的概率使用 [动作 ${action_detail_2 % 10}]"
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
                    trueClause = if (action_detail_1 == 710 || action_detail_1 == 100) {
                        if (status != "")
                            "当${getTarget()}在[${status}]的场合，使用 [动作 ${action_detail_2 % 100}]"
                        else
                            ""
                    } else if (action_detail_1 in 0..99) {
                        "以 [$action_detail_1%] 的概率使用 [动作 ${action_detail_2 % 10}]"
                    } else if (action_detail_1 == 599) {
                        "${getTarget()}身上有持续伤害的场合，使用 [动作 ${action_detail_2 % 10}]"
                    } else if (action_detail_1 in 600..699) {
                        "${getTarget()}的标记 [ID: ${action_detail_1 - 600}] 层数在 [${action_value_3.int}] 以上的场合，使用 [动作 ${action_detail_2 % 10}]"
                    } else if (action_detail_1 == 700) {
                        "${getTarget()}是单独的场合，使用 [${action_detail_2 % 10}]"
                    } else if (action_detail_1 in 701..709) {
                        "排除潜伏状态的单位，${getTarget()}的数量是 [${action_detail_1 - 700}] 的场合，使用 [动作 ${action_detail_2 % 10}]"
                    } else if (action_detail_1 == 720) {
                        "排除潜伏状态的单位，${getTarget()}中存在 [ID: ${action_value_3.int}] 的单位的场合，使用 [动作 ${action_detail_2 % 10}]"
                    } else if (action_detail_1 in 901..999) {
                        "${getTarget()}的HP在 [${action_detail_1 - 900}%] 以下的场合，使用 [动作 ${action_detail_2 % 10}]"
                    } else if (action_detail_1 == 1000) {
                        "上一个动作击杀了单位的场合，使用 [动作 ${action_detail_2 % 10}]"
                    } else if (action_detail_1 == 1001) {
                        "本技能触发暴击的场合，使用 [动作 ${action_detail_2 % 10}]"
                    } else if (action_detail_1 in 1200..1299) {
                        "[计数器 ${action_detail_1 % 100 / 10}] 的数量在 [${action_detail_1 % 10}] 以上的场合，使用 [动作 ${action_detail_2 % 10}]"
                    } else {
                        ""
                    }
                }

                if (action_detail_3 != 0) {
                    falseClause = if (action_detail_1 == 710) {
                        if (status != "")
                            "当${getTarget()}不在[${status}]的场合，使用 [动作 ${action_detail_3 % 100}]"
                        else
                            ""
                    } else if (action_detail_1 in 0..99) {
                        "以 [100 - $action_detail_1%] 的概率使用 [动作 ${action_detail_3 % 10}]"
                    } else if (action_detail_1 == 599) {
                        "${getTarget()}身上没有持续伤害的场合，使用 [动作 ${action_detail_3 % 10}]"
                    } else if (action_detail_1 in 600..699) {
                        "${getTarget()}的标记 [ID: ${action_detail_1 - 600}] 层数不足 [${action_value_3.int}] 的场合，使用 [动作 ${action_detail_3 % 10}]"
                    } else if (action_detail_1 == 700) {
                        "${getTarget()}是不是单独的场合，使用 [${action_detail_3 % 10}]"
                    } else if (action_detail_1 in 701..709) {
                        "排除潜伏状态的单位，${getTarget()}的数量不是 [${action_detail_1 - 700}] 的场合，使用 [动作 ${action_detail_3 % 10}]"
                    } else if (action_detail_1 == 720) {
                        "排除潜伏状态的单位，${getTarget()}中不存在 [ID: ${action_value_3.int}] 的单位的场合，使用 [动作 ${action_detail_3 % 10}]"
                    } else if (action_detail_1 in 901..999) {
                        "${getTarget()}的HP不在 [${action_detail_1 - 900}%] 以下的场合，使用 [动作 ${action_detail_3 % 10}]"
                    } else if (action_detail_1 == 1000) {
                        "上一个动作未击杀单位的场合，使用 [动作 ${action_detail_3 % 10}]"
                    } else if (action_detail_1 == 1001) {
                        "本技能未触发暴击的场合，使用 [动作 ${action_detail_3 % 10}]"
                    } else if (action_detail_1 in 1200..1299) {
                        "[计数器 ${action_detail_1 % 100 / 10}] 的数量不足 [${action_detail_1 % 10}] 的场合，使用 [动作 ${action_detail_3 % 10}]"
                    } else {
                        ""
                    }
                }

                //条件
                if (trueClause != "" && falseClause != "")
                    "全体条件分歧：${trueClause}${falseClause}"
                else if (trueClause != "")
                    "全体条件分歧：${trueClause}"
                else if (falseClause != "")
                    "全体条件分歧：${falseClause}"
                else
                    ""
            }
            SkillActionType.REVIVAL -> {
                "复活${getTarget()}，并回复其 [${(action_value_2 * 100).int}]HP"
            }
            SkillActionType.CONTINUOUS_ATTACK -> ""
            SkillActionType.ADDITIVE -> {
                val type = when (action_detail_1) {
                    7 -> "物理攻击力"
                    8 -> "魔法攻击力"
                    9 -> "物理防御力"
                    10 -> "魔法防御力"
                    else -> ""
                }
                val commonExpr = if (action_detail_3 == 0) {
                    "[${action_value_2}]"
                } else if (action_detail_2 == 0) {
                    "[${action_value_3}]"
                } else {
                    "[${(action_value_2 + action_value_3 * level)}] <$action_value_2 + $action_value_3 * 技能等级> "
                }
                val commonDesc =
                    "[动作 ${action_detail_1 % 10}] 的系数 [${action_detail_2}] 提高 $commonExpr"

                val additive = when (action_value_1.int) {
                    2 -> {
                        val expr = if (action_detail_3 == 0) {
                            "[${action_value_2}]"
                        } else if (action_detail_2 == 0) {
                            "[${action_value_3}]"
                        } else {
                            "[${(action_value_2 + 2 * action_value_3 * level)}] <$action_value_2 + ${2 * action_value_3} * 技能等级> "
                        }
                        "[动作 ${action_detail_1 % 10}] 的系数 [${action_detail_2}] 提高 $expr * 击杀的敌方数量"
                    }
                    0 -> "$commonDesc * HP"
                    1 -> "$commonDesc * 损失的HP"
                    4 -> "$commonDesc * 目标的数量"
                    5 -> "$commonDesc * 受到伤害的目标数量"
                    6 -> "$commonDesc * 造成的伤害"
                    12 -> "$commonDesc * 后方${getTarget()}数量"
                    102 -> "$commonDesc * 小眼球数量"
                    in 200 until 300 -> "$commonDesc * 标记 [ID: ${action_value_1.int % 200}] 的层数"
                    in 7..10 -> "$commonDesc * ${getTarget()}的$type"
                    in 20 until 30 -> "$commonDesc * 计数器${action_value_1.int % 10}的数值"
                    else -> ""
                }
                additive
                //fixme 上限判断
//                val limit = "提高值的上限为 [%s]"
//                if (action_value_4.int != 0 && action_value_5.int != 0)
//                    desc + limit
//                else
//                    desc
            }
            SkillActionType.MULTIPLE -> {
                val expr = if (action_value_3.int != 0) {
                    "[${(action_value_2 + action_value_3 * level)}] <$action_value_2 + $action_value_3 * 技能等级> "
                } else {
                    "[$action_value_2]"
                }
                val commonDesc = "[动作 ${action_detail_1 % 10}] 的系数 [$action_detail_2] * $expr"
                when (action_detail_1) {
                    0 -> "$commonDesc * 剩余HP比例"
                    1 -> "$commonDesc * 击杀的敌方数量"
                    in 200 until 300 -> "$commonDesc * 标记 [ID: ${action_value_1.int % 200}]"
                    else -> ""
                }
            }
            SkillActionType.CHANGE_SEARCH_AREA -> ""
            SkillActionType.KILL_ME -> "${getTargetType()}死亡"
            SkillActionType.CONTINUOUS_ATTACK_NEARBY -> ""
            SkillActionType.LIFE_STEAL -> {
                "为${getTarget()}的下 [${action_value_3.int}] 次攻击附加 ${toSkillActionType(action_type).desc}[${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> 效果"
            }
            SkillActionType.STRIKE_BACK -> {
                val expr =
                    "[${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级>"
                val shieldText = "对${getTarget()}展开"
                when (action_detail_1) {
                    1 -> "${shieldText}承受物理伤害的护盾${expr}，受到物理伤害时反射 [${action_value_3}] 伤害"
                    2 -> "${shieldText}承受魔法伤害的护盾${expr}，受到魔法伤害时反射 [${action_value_3}] 伤害"
                    3 -> "${shieldText}物理伤害无效的护盾${expr}，受到物理伤害时反射 [${action_value_3}] 伤害，并回复HP"
                    4 -> "${shieldText}魔法伤害无效的护盾${expr}，受到魔法伤害时反射 [${action_value_3}] 伤害，并回复HP"
                    5 -> "${shieldText}承受所有伤害的护盾${expr}，受到伤害时反射 [${action_value_3}] 伤害"
                    6 -> "${shieldText}所有伤害无效的护盾${expr}，受到伤害时反射 [${action_value_3}] 伤害，并回复HP"
                    else -> ""
                }
            }
            SkillActionType.ACCUMULATIVE_DAMAGE -> {
                "每次攻击当前的目标，将会追加 [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> , 叠加上限 [${(action_value_4).int}]"
            }
            SkillActionType.SEAL -> {
                if (action_value_4.int > 0) {
                    "对${getTarget()}追加 [${action_value_4.int}] 层标记 [ID: ${action_value_2.int}]，持续时间 [${action_value_3.int}] 秒，最大叠 [${action_value_1.int}] 层"
                } else {
                    "${getTarget()}的标记 [ID: ${action_value_2.int}] 层数减少 [${action_value_4.int}] 层"
                }
            }
            SkillActionType.ATTACK_FIELD -> {
                val atkType = if (action_detail_1 % 2 == 0) "魔法" else "物理"
                val value = "，每秒造成 ${
                    getValueText(
                        action_value_1,
                        action_value_2,
                        action_value_3
                    )
                } ${atkType}伤害"
                "展开半径为 [${action_value_7.int}] 的领域${value}${
                    getTimeText(
                        action_value_5,
                        action_value_6
                    )
                }"
            }
            SkillActionType.HEAL_FIELD -> {
                val value =
                    "，每秒回复 ${getValueText(action_value_1, action_value_2, action_value_3)} HP"
                "展开半径为 [${action_value_7.int}] 的领域${value}${
                    getTimeText(
                        action_value_5,
                        action_value_6
                    )
                }"
            }
            SkillActionType.DEBUFF_FIELD -> {
                "${getTarget()} 展开半径为 [${action_value_5.int}] 的领域，${getAura(action_detail_1)} ${
                    getValueText(
                        action_value_1,
                        action_value_2,
                        0.0
                    )
                }${getTimeText(action_value_3, action_value_4)}"
            }
            SkillActionType.DOT_FIELD -> {
                if (action_value_2 == 0.toDouble()) {
                    "$action_value_1 ，持续 [${action_value_4}] 秒"
                } else {
                    " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> ，持续 [${action_value_3}] 秒"
                } + "，范围 <$action_value_7> ；"
            }
            SkillActionType.CHANNEL -> {
                " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> ，持续 [${action_value_4}] 秒"
            }
            SkillActionType.EX -> {
                val name = when (action_detail_1) {
                    1 -> "HP"
                    2 -> "物理攻击力"
                    3 -> "物理防御力"
                    4 -> "魔法攻击力"
                    5 -> "魔法防御力"
                    else -> ""
                }

                "自身的${name}提升     ${getValueText(action_value_2, action_value_3, 0.0)}"
            }
            else -> ""
        }

        val action = when {
            description.contains("0") -> description.replace("{0}", desc)
            else -> description.plus(desc)
        }

        return SkillActionText(
            tag,
            action_id.toString() + "\n" + action + "\n" + formatDesc,
            summonUnitId
        )
    }


    private fun getTimeText(v1: Double, v2: Double): String {
        return if (v2 > 0)
            ", 持续 [${v1 + v2 * level}] <$v1 + $v2 * 技能等级> 秒"
        else
            ", 持续 [$v1] 秒"
    }

    private fun getValueText(v1: Double, v2: Double, v3: Double, percent: String = ""): String {
        return if (v2.int == 0) {
            "[${v1.int}$percent]"
        } else if (v3.int == 0) {
            "[${(v1 + v2 * level).int}$percent] <$v1 + $v2 * 技能等级>"
        } else {
            "[${(v1 + v2 * level + v3 * atk).int}$percent] <$v1 + $v2 * 技能等级 + $v3 * 攻击力>"
        }
    }

    private fun getAura(v: Int): String {
        return when (v / 10) {
            1 -> "物理攻击力"
            2 -> "物理防御力"
            3 -> "魔法攻击力"
            4 -> "魔法防御力"
            5 -> "回避"
            6 -> "物理暴击"
            7 -> "魔法暴击"
            8 -> "TP上升"
            9 -> "生命吸收"
            10 -> "速度"
            11 -> "物理暴击伤害"
            12 -> "魔法暴击伤害"
            13 -> "命中"
            14 -> "受到的暴击伤害"
            100 -> "HP"
            else -> ""
        } + if (v % 10 == 0) "提升" else "下降"
    }
}

/**
 * 简化技能效果数据
 */
data class SkillActionText(
    val tag: String,
    val action: String,
    val summonUnitId: Int
)

//    val desc = when (toSkillActionType(action_type)) {
//            SkillActionType.DAMAGE, SkillActionType.HOT -> {
//                " [${(action_value_1 + action_value_2 * level + action_value_3 * atk).int}] <$action_value_1 + $action_value_2 * 技能等级 + $action_value_3 * 攻击力> "
//            }
//            SkillActionType.MOVE -> {
//                "移动至最近敌人前 [$action_value_1] " + if (action_value_2 > 0) "，移动速度 [${action_value_2}] " else ""
//            }
//            SkillActionType.CHANGE_ENEMY_POSITION -> {
//                ailmentName = if (action_detail_2 == 0) {
//                    "击飞"
//                } else {
//                    if (action_value_1 > 0) "击退" else "拉近"
//                }
//                "${ailmentName} [${(abs(action_value_4)).int}}] "
//            }
//            SkillActionType.HEAL -> {
//                " [${(action_value_2 + action_value_3 * level + action_value_4 * atk).int}] <$action_value_2 + $action_value_3 * 技能等级 + $action_value_4 * 攻击力> "
//            }
//            SkillActionType.SHIELD -> {
//                " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> ，持续 [${action_value_3}] 秒"
//            }
//            SkillActionType.CHANGE_ACTION_SPEED -> {
//                //判断异常状态
//                ailmentName = when (action_detail_1) {
//                    1 -> "减速"
//                    2 -> "加速"
//                    3 -> "麻痹"
//                    4 -> "冰冻"
//                    5 -> "束缚"
//                    6 -> "睡眠"
//                    7 -> "眩晕"
//                    8 -> "石化"
//                    9 -> "拘留"
//                    10 -> "晕倒"
//                    11 -> "时停"
//                    else -> ""
//                }
//                if (action_value_1 != 0.toDouble()) {
//                    "，速度 *  [${action_value_1}] ， 持续 [${action_value_3}] 秒"
//                } else {
//                    "，持续 [${action_value_3}] 秒"
//                }
//            }
//            SkillActionType.DOT -> {
//                ailmentName = when (action_detail_1) {
//                    0 -> "拘留（造成伤害）"
//                    1 -> "毒"
//                    2 -> "烧伤"
//                    3, 5 -> "诅咒"
//                    4 -> "猛毒"
//                    else -> ""
//                }
//                " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> ，持续 [${action_value_3}] 秒"
//            }
//            SkillActionType.AURA -> {
//                ailmentName = if (target_assignment == 1) "DEBUFF" else "BUFF"
//                if (action_value_3 > 0) {
//                    " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> "
//                } else {
//                    " [${(action_value_2).int}] "
//                } + "，持续 [${action_value_4}] 秒"
//            }
//            SkillActionType.CHARM -> {
//                ailmentName = when (action_detail_1) {
//                    0 -> "魅惑"
//                    1 -> "混乱"
//                    else -> ""
//                }
//                "，持续 [${action_value_1}] 秒，" + if (action_value_3 == 100.toDouble()) "成功率 [100] " else "成功率 [${(1 + action_value_3 * level).int}] <1 + $action_value_3 * 技能等级> "
//            }
//            SkillActionType.BLIND -> {
//                "，持续 [${action_value_1}] 秒，" + if (action_value_3 == 100.toDouble()) "成功率 [100] " else "成功率 [${(1 + action_value_3 * level).int}] <1 + $action_value_3 * 技能等级> "
//            }
//            SkillActionType.CHANGE_MODE -> {
//                if (action_value_1 > 0) "每秒降低TP  [${(action_value_1).int}] " else ""
//            }
//            SkillActionType.CHANGE_TP -> {
//                if (action_value_2 > 0)
//                    " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> "
//                else
//                    " [${(action_value_1).int}] "
//            }
//            SkillActionType.TAUNT, SkillActionType.INVINCIBLE -> {
//                if (action_value_2 > 0) ", 持续 [${action_value_1 + action_value_2 * level}] <$action_value_1 + $action_value_2 * 技能等级> 秒" else ", 持续 [${action_value_1}] 秒"
//            }
//            //反伤
//            SkillActionType.STRIKE_BACK -> {
//                " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> "
//            }
//            //伤害叠加
//            SkillActionType.INCREASED_DAMAGE -> {
//                " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> , 叠加上限 [${(action_value_4).int}] "
//            }
//            SkillActionType.ATTACK_FIELD -> {
//                " [${(action_value_1 + action_value_2 * level + action_value_3 * atk).int}] <$action_value_1 + $action_value_2 * 技能等级 + $action_value_3 * 攻击力> ，持续 [${action_value_5}] 秒，范围 [${(action_value_7).int}] "
//            }
//            SkillActionType.HEAL_FIELD,
//            SkillActionType.DEBUFF_FIELD,
//            SkillActionType.DOT_FIELD -> {
//                if (action_value_2 == 0.toDouble()) {
//                    "$action_value_1 ，持续 [${action_value_4}] 秒"
//                } else {
//                    " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> ，持续 [${action_value_3}] 秒"
//                } + "，范围 <$action_value_7> ；"
//            }
//            SkillActionType.CHANNEL -> {
//                " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> ，持续 [${action_value_4}] 秒"
//            }
//            SkillActionType.EX -> if (action_value_3 == 0.toDouble()) {
//                " [$action_value_2] "
//            } else {
//                " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> "
//            }
//            else -> ""
//        }
