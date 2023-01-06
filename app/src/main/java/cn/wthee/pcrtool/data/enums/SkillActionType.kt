package cn.wthee.pcrtool.data.enums

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.utils.getString

/**
 * 技能效果枚举
 *
 * @param type action_type
 * @param desc 技能标签
 * @see cn.wthee.pcrtool.data.db.view.SkillActionDetail.actionType
 */
enum class SkillActionType(val type: Int, val descId: Int) {

    UNKNOWN(0, R.string.none),

    /**
     * 1：造成伤害
     */
    DAMAGE(1, R.string.skill_type_1),

    /**
     * 2：位移
     */
    MOVE(2, R.string.skill_type_2),

    /**
     * 3：改变对方位置
     */
    CHANGE_ENEMY_POSITION(3, R.string.none),

    /**
     * 4：回复 HP
     */
    HEAL(4, R.string.skill_type_4_5),

    /**
     * 5：回复 HP
     */
    CURE(5, R.string.skill_type_4_5),

    /**
     * 6：护盾
     */
    BARRIER(6, R.string.skill_type_6),

    /**
     * 7：指定攻击对象
     */
    CHOOSE_ENEMY(7, R.string.none),

    /**
     * 8：行动速度变更
     */
    CHANGE_ACTION_SPEED(8, R.string.none),

    /**
     * 9：持续伤害
     */
    DOT(9, R.string.none),

    /**
     * 10：buff/debuff
     */
    AURA(10, R.string.none),

    /**
     * 11：魅惑/混乱
     */
    CHARM(11, R.string.none),

    /**
     * 12：黑暗
     */
    BLIND(12, R.string.skill_type_12),

    /**
     * 13：沉默
     */
    SILENCE(13, R.string.skill_type_13),

    /**
     * 14：行动模式变更
     */
    CHANGE_MODE(14, R.string.skill_type_14),

    /**
     * 15：召唤
     */
    SUMMON(15, R.string.skill_type_15),

    /**
     * 16：TP 相关
     */
    CHANGE_TP(16, R.string.skill_type_16_92),

    /**
     * 17：触发条件
     */
    TRIGGER(17, R.string.skill_type_17),

    /**
     * 18：蓄力
     */
    CHARGE(18, R.string.skill_type_18_19),

    /**
     * 19：伤害充能
     */
    DAMAGE_CHARGE(19, R.string.skill_type_18_19),

    /**
     * 20：挑衅
     */
    TAUNT(20, R.string.skill_type_20),

    /**
     * 21：回避
     */
    INVINCIBLE(21, R.string.skill_type_21),

    /**
     * 22：改变模式
     */
    CHANGE_PATTERN(22, R.string.skill_type_22),

    /**
     * 23：判定对象状态
     */
    IF_STATUS(23, R.string.none),

    /**
     * 24：复活
     */
    REVIVAL(24, R.string.skill_type_24),

    /**
     * 25：连续攻击
     */
    CONTINUOUS_ATTACK(25, R.string.none),

    /**
     * 26：系数提升
     */
    ADDITIVE(26, R.string.none),

    /**
     * 27：倍率
     */
    MULTIPLE(27, R.string.none),

    /**
     * 28：特殊条件
     */
    IF_SP_STATUS(28, R.string.none),

    /**
     * 29：无法使用 UB
     */
    NO_UB(29, R.string.none),

    /**
     * 30：立即死亡
     */
    KILL_ME(30, R.string.skill_type_30),

    /**
     * 31：连续攻击附近
     */
    CONTINUOUS_ATTACK_NEARBY(31, R.string.none),

    /**
     * 32：HP吸收
     */
    LIFE_STEAL(32, R.string.skill_type_32),

    /**
     * 33：反伤
     */
    STRIKE_BACK(33, R.string.skill_type_33),

    /**
     * 34：伤害递增
     */
    ACCUMULATIVE_DAMAGE(34, R.string.skill_type_34),

    /**
     * 35：特殊标记
     */
    SEAL(35, R.string.skill_type_35_43_60_77),

    /**
     * 36：攻击领域展开
     */
    ATTACK_FIELD(36, R.string.skill_type_36_37_38_39_40),

    /**
     * 37：治疗领域展开
     */
    HEAL_FIELD(37, R.string.skill_type_36_37_38_39_40),

    /**
     * 38：buff/debuff领域展开
     */
    AURA_FIELD(38, R.string.skill_type_36_37_38_39_40),

    /**
     * 39：持续伤害领域展开
     */
    DOT_FIELD(39, R.string.skill_type_36_37_38_39_40),

    /**
     * 40：范围行动速度变更
     */
    CHANGE_ACTION_SPEED_FIELD(40, R.string.skill_type_36_37_38_39_40),

    /**
     * 41：改变 UB 时间
     */
    CHANGE_UB_TIME(41, R.string.none),

    /**
     * 42：触发
     */
    LOOP_TRIGGER(42,  R.string.skill_type_42),

    /**
     * 43：拥有标记时触发
     */
    IF_TARGETED(43, R.string.skill_type_35_43_60_77),

    /**
     * 44：每场战斗开始时
     */
    WAVE_START(44,  R.string.skill_type_44),

    /**
     * 45：已使用技能数相关
     */
    SKILL_COUNT(45, R.string.none),

    /**
     * 46：比例伤害
     */
    RATE_DAMAGE(46,  R.string.skill_type_46),

    /**
     * 47：上限伤害
     */
    UPPER_LIMIT_ATTACK(47, R.string.none),

    /**
     * 48：持续治疗
     */
    HOT(48,  R.string.skill_type_48),

    /**
     * 49：移除增益
     */
    DISPEL(49,  R.string.skill_type_49),

    /**
     * 50：持续动作
     */
    CHANNEL(50,  R.string.skill_type_50),

    /**
     * 51：分裂
     */
    DIVISION(51, R.string.none),

    /**
     * 52：改变单位距离
     */
    CHANGE_WIDTH(52, R.string.none),

    /**
     * 53：特殊状态：领域存在时；如：情姐
     */
    IF_HAS_FIELD(53, R.string.none),

    /**
     * 54：潜伏
     */
    STEALTH(54,  R.string.skill_type_54),

    /**
     * 55：部位移动
     */
    MOVE_PART(55, R.string.none),

    /**
     * 56：千里眼
     */
    COUNT_BLIND(56,  R.string.skill_type_56),

    /**
     * 57：延迟攻击 如：万圣炸弹人的 UB
     */
    COUNT_DOWN(57,  R.string.skill_type_57),

    /**
     * 58：解除领域 如：晶姐 UB
     */
    STOP_FIELD(58,  R.string.skill_type_58),

    /**
     * 59：回复妨碍
     */
    INHIBIT_HEAL_ACTION(59,  R.string.skill_type_59),

    /**
     * 60：标记赋予
     */
    ATTACK_SEAL(60, R.string.skill_type_35_43_60_77),

    /**
     * 61：恐慌
     */
    FEAR(61,  R.string.skill_type_61),

    /**
     * 62：畏惧
     */
    AWE(62,  R.string.skill_type_62),

    /**
     * 63: 循环动作
     */
    LOOP(63,  R.string.skill_type_63),

    /**
     * 69：变身
     */
    REINDEER(69,  R.string.skill_type_69),

    /**
     * 70：HP 变化
     */
    HP_CHANGE(70,  R.string.skill_type_70),

    /**
     * 71：特殊状态：公主佩可 UB 后不死BUFF
     */
    KNIGHT_BARRIER(71,  R.string.skill_type_71),

    /**
     * 72：伤害减免
     */
    DAMAGE_REDUCE(72,  R.string.skill_type_72),

    /**
     * 73：伤害护盾
     */
    LOG_BARRIER(73,  R.string.skill_type_73),

    /**
     * 74：系数除以
     */
    DIVIDE(74, R.string.none),

    /**
     * 75：依据攻击次数增伤
     */
    HIT_COUNT(75,  R.string.skill_type_75),

    /**
     * 76：HP 回复量减少
     */
    HEAL_DOWN(76,  R.string.skill_type_76),

    /**
     * 77：被动叠加标记
     */
    IF_BUFF_SEAL(77, R.string.skill_type_35_43_60_77),

    /**
     * 78：被击伤害上升
     */
    DMG_TAKEN_UP(78,  R.string.skill_type_78),

    /**
     * 79：行动时，造成伤害
     */
    ACTION_DOT(79, R.string.skill_type_79),

    /**
     * 81：无效目标
     */
    NO_TARGET(81, R.string.skill_type_81),

    /**
     * 83：可叠加行动速度变更
     */
    SUPERIMPOSE_CHANGE_ACTION_SPEED(83, R.string.none),

    /**
     * 90：EX被动
     */
    EX(90, R.string.skill_type_90),

    /**
     * 901：ex装备被动被动
     */
    EX_EQUIP(901, R.string.skill_type_901_902),
    EX_EQUIP_HALF(902, R.string.skill_type_901_902),

    /**
     * 92：改变 TP 获取倍率
     */
    CHANGE_TP_RATIO(92, R.string.skill_type_16_92),

    /**
     * 93：无视挑衅
     */
    IGNOR_TAUNT(93, R.string.none),

    /**
     * 94：技能特效
     */
    SPECIAL_EFFECT(94, R.string.none),

    /**
     * 95：隐匿
     */
    HIDE(95, R.string.skill_type_95),

    /**
     * 96：范围tp回复
     */
    TP_FIELD(96, R.string.none),

    /**
     * 97：受击tp回复
     */
    TP_HIT(97, R.string.none),

    /**
     * 98：改变 TP 减少时倍率
     */
    TP_HIT_REDUCE(98, R.string.none),

    /**
     * 99：范围加速
     */
    SPEED_FIELD(99, R.string.none),
}

/**
 * 获取描述
 */
fun getAilment(value: Int): String {
    for (item in SkillActionType.values()) {
        if (item.type == value) {
            return getString(item.descId)
        }
    }
    return ""
}

/**
 * 获取技能类型枚举对象
 */
fun toSkillActionType(value: Int): SkillActionType {
    for (item in SkillActionType.values()) {
        if (item.type == value) return item
    }
    return SkillActionType.UNKNOWN
}


