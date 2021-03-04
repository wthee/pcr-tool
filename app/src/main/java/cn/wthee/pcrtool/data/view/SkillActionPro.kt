package cn.wthee.pcrtool.data.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.data.enums.SkillActionType
import cn.wthee.pcrtool.data.enums.getAilment
import cn.wthee.pcrtool.data.enums.toSkillActionType
import cn.wthee.pcrtool.data.model.int
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
    @ColumnInfo(name = "ailment_name") var ailmentName: String
) {

    /**
     * 获取技能效果
     *
     * fixme 优化判断逻辑
     */
    fun getFixedDesc(): SkillActionLite {
        val fixed: String

        /**
         * 技能施放对象
         *  [target_assignment]
         * 0：自身 1：敌方 2：自身或己方
         *
         * 技能施放方向
         *  [target_area]
         * 1：前方 2：前后 3：全体
         *
         * 技能施放对象数量
         *  [target_count]
         * 0,1,2,3 99：范围内全体
         *
         * 技能范围
         *  [target_range]
         * >  2160: 全体
         * <2160 ：范围
         */
        //设置状态标签
        val p = getAilment(action_type)
        if (p.isNotEmpty()) {
            ailmentName = p
        }
        val desc = when (toSkillActionType(action_type)) {
            SkillActionType.DAMAGE, SkillActionType.HOT -> {
                " [${(action_value_1 + action_value_2 * level + action_value_3 * atk).int}] <$action_value_1 + $action_value_2 * 技能等级 + $action_value_3 * 攻击力> "
            }
            SkillActionType.MOVE -> {
                "移动至最近敌人前 [$action_value_1] " + if (action_value_2 > 0) "，移动速度 [${action_value_2}] " else ""
            }
            SkillActionType.CHANGE_ENEMY_POSITION -> {
                ailmentName = if (action_detail_2 == 0) {
                    "击飞"
                } else {
                    if (action_value_1 > 0) "击退" else "拉近"
                }
                "${ailmentName} [${(abs(action_value_4)).int}}] "
            }
            SkillActionType.HEAL -> {
                " [${(action_value_2 + action_value_3 * level + action_value_4 * atk).int}] <$action_value_2 + $action_value_3 * 技能等级 + $action_value_4 * 攻击力> "
            }
            SkillActionType.SHIELD -> {
                " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级] ，持续 [${(action_value_3).int}] 秒"
            }
            SkillActionType.CHANGE_ACTION_SPEED -> {
                //判断异常状态
                ailmentName = when (action_detail_1) {
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
                    "，速度 *  [${(action_value_1).int}] ， 持续 [${(action_value_3).int}] 秒"
                } else {
                    "，持续 [${(action_value_3).int}] 秒"
                }
            }
            SkillActionType.DOT -> {
                ailmentName = when (action_detail_1) {
                    0 -> "拘留（造成伤害）"
                    1 -> "毒"
                    2 -> "烧伤"
                    3, 5 -> "诅咒"
                    4 -> "猛毒"
                    else -> ""
                }
                " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> ，持续 [${(action_value_3).int}] 秒"
            }
            SkillActionType.AURA -> {
                ailmentName = if (target_assignment == 1) "DEBUFF" else "BUFF"
                if (action_value_3 > 0) {
                    " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> "
                } else {
                    " [${(action_value_2).int}] "
                } + "，持续 [${(action_value_4).int}] 秒"
            }
            SkillActionType.CHARM -> {
                ailmentName = when (action_detail_1) {
                    0 -> "魅惑"
                    1 -> "混乱"
                    else -> ""
                }
                "，持续 [${(action_value_1).int}] 秒，" + if (action_value_3 == 100.toDouble()) "成功率 [100] " else "成功率 [${(1 + action_value_3 * level).int}] <1 + $action_value_3 * 技能等级> "
            }
            SkillActionType.BLIND -> {
                "，持续 [${(action_value_1).int}] 秒，" + if (action_value_3 == 100.toDouble()) "成功率 [100] " else "成功率 [${(1 + action_value_3 * level).int}] <1 + $action_value_3 * 技能等级> "
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
                if (action_value_2 > 0) ", 持续 [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> 秒" else ", 持续 [${(action_value_1).int}] 秒"
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
                " [${(action_value_1 + action_value_2 * level + action_value_3 * atk).int}] <$action_value_1 + $action_value_2 * 技能等级 + $action_value_3 * 攻击力> ，持续 [${(action_value_5).int}] 秒，范围 [${(action_value_7).int}] "
            }
            SkillActionType.HEAL_FIELD,
            SkillActionType.DEBUFF_FIELD,
            SkillActionType.DOT_FIELD -> {
                if (action_value_2 == 0.toDouble()) {
                    "$action_value_1 ，持续 [${(action_value_4).int}] 秒"
                } else {
                    " [${(action_value_1 + action_value_2 * level).int}] <$action_value_1 + $action_value_2 * 技能等级> ，持续 [${(action_value_3).int}] 秒"
                } + "，范围 <$action_value_7> ；"
            }
            SkillActionType.CHANNEL -> {
                " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> ，持续 [${(action_value_4).int}] 秒"
            }
            SkillActionType.EX -> if (action_value_3 == 0.toDouble()) {
                " [$action_value_2] "
            } else {
                " [${(action_value_2 + action_value_3 * level).int}] <$action_value_2 + $action_value_3 * 技能等级> "
            }
            else -> ""
        }
        fixed = when {
            description.contains("0") -> description.replace("{0}", desc)
            else -> description.plus(desc)
        }

        return SkillActionLite(ailmentName, fixed)
    }
}

/**
 * 简化技能效果数据
 */
data class SkillActionLite(
    val ailmentName: String,
    val action: String
)