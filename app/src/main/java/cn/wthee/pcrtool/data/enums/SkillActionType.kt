package cn.wthee.pcrtool.data.enums

/**
 * 技能效果枚举
 *
 * @param type action_type
 * @param desc 技能标签
 * @sample cn.wthee.pcrtool.data.db.entity.SkillAction.action_type
 */
enum class SkillActionType(val type: Int, val desc: String) {

    UNKNOWN(0, ""),

    /**
     * 1：造成伤害
     */
    DAMAGE(1, "伤害"),

    /**
     * 2：冲锋
     */
    MOVE(2, "冲锋"),

    /**
     * 3：改变对方位置
     */
    CHANGE_ENEMY_POSITION(3, ""),

    /**
     * 4：回复HP
     */
    HEAL(4, "治疗"),

    /**
     * 5：回复HP
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
     * 8：行动速度变更：行动速度提升/降低；无法行动
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
     * 12：黑暗，失明
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
     * 16：TP相关
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
     * 21：无敌
     */
    INVINCIBLE(21, "回避"),

    /**
     * 22：改变模式
     */
    CHANGE_PATTERN(22, "行动变更"),

    /**
     * 23：判定对象状态
     */
    IF_FOR_CHILDREN(23, ""),

    /**
     * 24：复活
     */
    REVIVAL(24, "复活"),

    /**
     * 25：连续攻击
     */
    CONTINUOUS_ATTACK(25, ""),

    /**
     * 26：增伤
     */
    ADDITIVE(26, "增伤"),

    /**
     * 27：倍率
     */
    MULTIPLE(27, "倍率"),

    /**
     * 28：特殊条件
     *
     */
    IF_FOR_ALL(28, ""),

    /**
     * 29：变更攻击区域？
     */
    CHANGE_SEARCH_AREA(29, ""),

    /**
     * 30：立即死亡
     */
    KILL_ME(30, "即死"),

    /**
     * 31：连续攻击附近
     */
    CONTINUOUS_ATTACK_NEARBY(31, ""),

    /**
     * 32：吸血效果
     */
    LIFE_STEAL(32, "HP吸收"),

    /**
     * 33：消失时造成伤害
     */
    STRIKE_BACK(33, "反伤"),

    /**
     * 34：伤害递增
     */
    ACCUMULATIVE_DAMAGE(34, "伤害递增"),

    /**
     * 35：特殊刻印
     */
    SEAL(35, "刻印"),

    /**
     * 36：范围攻击
     */
    ATTACK_FIELD(36, "领域"),

    /**
     * 37：范围治疗
     */
    HEAL_FIELD(37, "领域"),

    /**
     * 38：范围减益
     */
    AURA_FIELD(38, "领域"),

    /**
     * 39：范围持续伤害
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
     * 42：循环触发：哈哈剑大笑时...等状态触发
     */
    LOOP_TRIGGER(42, "条件"),

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
    RATE_DAMAGE(46, ""),

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
     * 50：特殊状态：铃声响起时
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
     * 54：隐身
     */
    STEALTH(54, "隐身"),

    /**
     * 55：部位移动
     */
    MOVE_PART(55, ""),

    /**
     * 56：闪避
     */
    COUNT_BLIND(56, "闪避"),

    /**
     * 57：延迟攻击 如：万圣炸弹人的 UB
     */
    COUNT_DOWN(57, "延时"),

    /**
     * 58：解除领域 如：晶姐 UB
     */
    STOP_FIELD(58, "解除领域"),

    /**
     * 59：降低治疗效果
     */
    INHIBIT_HEAL_ACTION(59, "减疗"),

    /**
     * 60：攻击刻印 华哥
     */
    ATTACK_SEAL(60, "刻印"),

    /**
     * 61：恐惧
     */
    FEAR(61, "恐惧"),

    /**
     * 62：敬畏
     */
    AWE(62, "敬畏"),


    /**
     * 63: 循环
     */
    LOOP(63, ""),

    /**
     * 蛤？
     */
    TOAD(69, ""),

    /**
     * 71：特殊状态：公主佩可 UB 后不死BUFF
     */
    KNIGHT_GUARD(71, "骑士"),

    /**
     * 伤害护盾
     */
    LOG_GUARD(73, "伤害护盾"),

    /**
     * 划分？
     */
    DIVIDE(74, ""),

    /**
     * 75：依据攻击次数增伤：水流夏
     */
    HIT_COUNT(75, "次数增伤"),


    /**
     * 76：减疗
     */
    HEAL_DOWN(76, "减疗"),


    /**
     * 77：特殊刻印：增益时叠加 圣诞哈哈剑
     */
    IF_BUFF_SEAL(77, "被动刻印"),

    /**
     * 79：行动时，造成伤害
     */
    ACTION_DOT(79, "行动伤害"),

    /**
     * 83：可叠加加/减速
     */
    SUPERIMPOSE_CHANGE_ACTION_SPEED(83, ""),

    /**
     * 90：EX被动
     */
    EX(90, "被动"),

    /**
     * 91：EX+被动
     */
    EX_PLUS(91, "被动"),

    /**
     * 92：EX+被动
     */
    CHANGE_TP_RATIO(92, ""),

    /**
     * 93：无视挑衅
     */
    IGNOR_TAUNT(93, ""),
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


