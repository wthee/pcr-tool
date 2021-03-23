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
        5, 26 -> "生命值比例最低的"
        6, 25 -> "生命值比例最高的"
        7 -> "自身"
        9 -> "最前方的"
        10 -> "最后方的"
        11 -> "范围内的"
        12, 27, 37 -> "TP 最高的"
        13, 19, 28 -> "TP 最低的"
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
        35 -> "生命值最高的"
        36 -> "生命值最低的"
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
                "${tag} [${(abs(action_value_4)).int}] "
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
                if (action_value_1 > 0) "每秒降低TP  [${(action_value_1).int}] " else ""
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
            SkillActionType.INCREASED_DAMAGE -> {
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
        //持续时间
        val timeText = "，持续 [${(action_value_3 + action_value_4 * level).int}] 秒"
        var summonUnitId = 0
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
                "恢复生命值 [${(action_value_2 + action_value_3 * level + action_value_4 * atk).int}] <$action_value_2 + $action_value_3 * 技能等级 + $action_value_4 * 攻击力> "
            }
            SkillActionType.SHIELD -> {
                val expr =
                    "[${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级>"
                val shieldText = "对${getTarget()}展开"
                val suffix = "的护盾${expr}${timeText}"
                when (action_detail_1) {
                    1 -> "${shieldText}承受物理伤害${suffix}"
                    2 -> "${shieldText}承受魔法伤害${suffix}"
                    3 -> "${shieldText}物理伤害无效${suffix}"
                    4 -> "${shieldText}魔法伤害无效${suffix}"
                    5 -> "${shieldText}所有伤害无效${suffix}"
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
                } + timeText
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
                val expr =
                    "[${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级>"
                "${tag}${getTarget()}${expr}${timeText}"
            }
            SkillActionType.AURA -> {
                tag = if (action_detail_1 % 10 == 0) "BUFF" else "DEBUFF"
                val aura = when (action_detail_1) {
                    10 -> "物理攻击力提升"
                    11 -> "物理攻击力下降"
                    20 -> "物理防御力提升"
                    21 -> "物理防御力下降"
                    30 -> "魔法攻击力提升"
                    31 -> "魔法攻击力下降"
                    40 -> "魔法防御力提升"
                    41 -> "魔法防御力下降"
                    else -> ""
                }
                "使${getTarget()}${aura}" + if (action_value_3 > 0) {
                    " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> "
                } else {
                    " [${(action_value_2).int}] "
                } + "，持续 [${(action_value_4 + action_value_5 * level).int}] 秒"
            }
            SkillActionType.CHARM -> {
                tag = when (action_detail_1) {
                    0 -> "魅惑"
                    1 -> "混乱"
                    else -> ""
                }
                "${tag}${getTarget()}，持续 [${action_value_1}] 秒，" + if (action_value_3 == 100.toDouble()) "成功率 [100] " else "成功率 [${(1 + action_value_3 * level).int}] <1 + $action_value_3 * 技能等级> "
            }
            SkillActionType.BLIND -> {
                "失明${getTarget()}，持续 [${action_value_1}] 秒，" + if (action_value_3 == 100.toDouble()) "成功率 [100] " else "成功率 [${(1 + action_value_3 * level).int}] <1 + $action_value_3 * 技能等级> "
            }
            SkillActionType.SILENCE -> {
                "${toSkillActionType(action_type).desc}${getTarget()}，成功率 [${action_value_3.int}]"
            }
            SkillActionType.CHANGE_PATTERN -> {
                when (action_detail_1) {
                    1 -> "技能循环改变，持续 [${action_value_1}] 秒"
                    2 -> "技能循环改变，每秒降低TP [${action_value_1}]"
                    3 -> "技能动画改变"
                    else -> ""
                }
            }
            SkillActionType.SUMMON -> {
                summonUnitId = action_detail_2
                if (action_value_7 > 0) {
                    "在${getTarget()}前方 [${action_value_7.int}] 的位置，召唤 [${action_detail_2}] 的召唤物"
                } else if (action_value_7 < 0) {
                    "在${getTarget()}后方 [${kotlin.math.abs(action_value_7).int}] 的位置，召唤 [${action_detail_2}] 的召唤物"
                } else {
                    "在${getTarget()}，召唤 [${action_detail_2}] 的召唤物。"
                }
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
            SkillActionType.INCREASED_DAMAGE -> {
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