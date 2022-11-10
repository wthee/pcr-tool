package cn.wthee.pcrtool.data.enums

/**
 * 技能效果枚举
 *
 * @param type action_type
 * @param desc 技能标签
 * @sample cn.wthee.pcrtool.data.db.view.SkillActionDetail.action_type
 */
enum class SkillActionType(val type: Int, val desc: String) {

    UNKNOWN(0, ""),

    /**
     * 1：造成伤害
     */
    DAMAGE(1, "伤害"),

    /**
     * 2：位移
     */
    MOVE(2, "位移"),

    /**
     * 3：改变对方位置
     */
    CHANGE_ENEMY_POSITION(3, ""),

    /**
     * 4：回复 HP
     */
    HEAL(4, "治疗"),

    /**
     * 5：回复 HP
     */
    CURE(5, "治疗"),

    /**
     * 6：护盾
     */
    BARRIER(6, "护盾"),

    /**
     * 7：指定攻击对象
     */
    CHOOSE_ENEMY(7, ""),

    /**
     * 8：行动速度变更
     */
    CHANGE_ACTION_SPEED(8, ""),

    /**
     * 9：持续伤害
     */
    DOT(9, ""),

    /**
     * 10：buff/debuff
     */
    AURA(10, ""),

    /**
     * 11：魅惑/混乱
     */
    CHARM(11, ""),

    /**
     * 12：黑暗
     */
    BLIND(12, "黑暗"),

    /**
     * 13：沉默
     */
    SILENCE(13, "沉默"),

    /**
     * 14：行动模式变更
     */
    CHANGE_MODE(14, "模式变更"),

    /**
     * 15：召唤
     */
    SUMMON(15, "召唤"),

    /**
     * 16：TP 相关
     */
    CHANGE_TP(16, "TP"),

    /**
     * 17：触发条件
     */
    TRIGGER(17, "条件"),

    /**
     * 18：蓄力
     */
    CHARGE(18, "蓄力"),

    /**
     * 19：伤害充能
     */
    DAMAGE_CHARGE(19, "蓄力"),

    /**
     * 20：挑衅
     */
    TAUNT(20, "挑衅"),

    /**
     * 21：回避
     */
    INVINCIBLE(21, "回避"),

    /**
     * 22：改变模式
     */
    CHANGE_PATTERN(22, "行动变更"),

    /**
     * 23：判定对象状态
     */
    IF_STATUS(23, ""),

    /**
     * 24：复活
     */
    REVIVAL(24, "复活"),

    /**
     * 25：连续攻击
     */
    CONTINUOUS_ATTACK(25, ""),

    /**
     * 26：系数提升
     */
    ADDITIVE(26, ""),

    /**
     * 27：倍率
     */
    MULTIPLE(27, ""),

    /**
     * 28：特殊条件
     */
    IF_SP_STATUS(28, ""),

    /**
     * 29：无法使用 UB
     */
    NO_UB(29, ""),

    /**
     * 30：立即死亡
     */
    KILL_ME(30, "即死"),

    /**
     * 31：连续攻击附近
     */
    CONTINUOUS_ATTACK_NEARBY(31, ""),

    /**
     * 32：HP吸收
     */
    LIFE_STEAL(32, "HP吸收"),

    /**
     * 33：反伤
     */
    STRIKE_BACK(33, "反伤"),

    /**
     * 34：伤害递增
     */
    ACCUMULATIVE_DAMAGE(34, "伤害递增"),

    /**
     * 35：特殊标记
     */
    SEAL(35, "标记"),

    /**
     * 36：攻击领域展开
     */
    ATTACK_FIELD(36, "领域"),

    /**
     * 37：治疗领域展开
     */
    HEAL_FIELD(37, "领域"),

    /**
     * 38：buff/debuff领域展开
     */
    AURA_FIELD(38, "领域"),

    /**
     * 39：持续伤害领域展开
     */
    DOT_FIELD(39, "领域"),

    /**
     * 40：范围行动速度变更
     */
    CHANGE_ACTION_SPEED_FIELD(40, "领域"),

    /**
     * 41：改变 UB 时间
     */
    CHANGE_UB_TIME(41, ""),

    /**
     * 42：触发
     */
    LOOP_TRIGGER(42, "触发"),

    /**
     * 43：拥有标记时触发
     */
    IF_TARGETED(43, ""),

    /**
     * 44：每场战斗开始时
     */
    WAVE_START(44, "进场"),

    /**
     * 45：已使用技能数相关
     */
    SKILL_COUNT(45, ""),

    /**
     * 46：比例伤害
     */
    RATE_DAMAGE(46, "比例伤害"),

    /**
     * 47：上限伤害
     */
    UPPER_LIMIT_ATTACK(47, ""),

    /**
     * 48：持续治疗
     */
    HOT(48, "持续回复"),

    /**
     * 49：移除增益
     */
    DISPEL(49, "移除"),

    /**
     * 50：持续动作
     */
    CHANNEL(50, "持续动作"),

    /**
     * 51：分裂
     */
    DIVISION(51, ""),

    /**
     * 52：改变单位距离
     */
    CHANGE_WIDTH(52, ""),

    /**
     * 53：特殊状态：领域存在时；如：情姐
     */
    IF_HAS_FIELD(53, ""),

    /**
     * 54：潜伏
     */
    STEALTH(54, "潜伏"),

    /**
     * 55：部位移动
     */
    MOVE_PART(55, ""),

    /**
     * 56：千里眼
     */
    COUNT_BLIND(56, "千里眼"),

    /**
     * 57：延迟攻击 如：万圣炸弹人的 UB
     */
    COUNT_DOWN(57, "延时"),

    /**
     * 58：解除领域 如：晶姐 UB
     */
    STOP_FIELD(58, "解除领域"),

    /**
     * 59：回复妨碍
     */
    INHIBIT_HEAL_ACTION(59, "回复妨碍"),

    /**
     * 60：标记赋予
     */
    ATTACK_SEAL(60, "标记赋予"),

    /**
     * 61：恐慌
     */
    FEAR(61, "恐慌"),

    /**
     * 62：畏惧
     */
    AWE(62, "畏惧"),

    /**
     * 63: 循环动作
     */
    LOOP(63, "持续效果"),

    /**
     * 69：驯鹿化
     */
    REINDEER(69, "驯鹿化"),

    /**
     * 70：HP 变化
     */
    HP_CHANGE(70, "HP变化"),

    /**
     * 71：特殊状态：公主佩可 UB 后不死BUFF
     */
    KNIGHT_GUARD(71, "骑士"),

    /**
     * 72：伤害减免
     */
    DAMAGE_REDUCE(72, "减伤"),

    /**
     * 73：伤害护盾
     */
    LOG_GUARD(73, "伤害护盾"),

    /**
     * 74：系数除以
     */
    DIVIDE(74, ""),

    /**
     * 75：依据攻击次数增伤
     */
    HIT_COUNT(75, "次数增伤"),

    /**
     * 76：HP 回复量减少
     */
    HEAL_DOWN(76, "HP回复量减少"),

    /**
     * 77：被动叠加标记
     */
    IF_BUFF_SEAL(77, "被动标记"),

    /**
     * 78：被击伤上升
     */
    DMG_TAKEN_UP(78, "被击伤上升"),

    /**
     * 79：行动时，造成伤害
     */
    ACTION_DOT(79, "行动伤害"),

    /**
     * 81：无效目标
     */
    NO_TARGET(81, "无效目标"),

    /**
     * 83：可叠加行动速度变更
     */
    SUPERIMPOSE_CHANGE_ACTION_SPEED(83, ""),

    /**
     * 90：EX被动
     */
    EX(90, "被动"),

    /**
     * 901：ex装备被动被动
     */
    EX_EQUIP(901, "装备"),
    EX_EQUIP_HALF(902, "装备"),

    /**
     * 92：改变 TP 获取倍率
     */
    CHANGE_TP_RATIO(92, "TP"),

    /**
     * 93：无视挑衅
     */
    IGNOR_TAUNT(93, ""),

    /**
     * 94：技能特效
     */
    SPECIAL_EFFECT(94, ""),

    /**
     * 95：隐匿
     */
    HIDE(95, "隐匿"),

    /**
     * 96：范围tp回复
     */
    TP_FIELD(96, ""),

    /**
     * 97：受击tp回复
     */
    TP_HIT(97, ""),
}

/**
 * 获取描述
 */
fun getAilment(value: Int): String {
    for (item in SkillActionType.values()) {
        if (item.type == value) return item.desc
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


