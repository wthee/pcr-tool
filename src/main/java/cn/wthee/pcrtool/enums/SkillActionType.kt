package cn.wthee.pcrtool.enums

/**
 * 技能效果枚举
 *
 * fixme 调整描述 [desc]
 */
enum class SkillActionType(val type: Int, val desc: String) {

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
     * 4：回复生命
     */
    HEAL(4, "治疗"),

    /**
     * 5：回复生命
     */
    CURE(5, "治疗"),

    /**
     * 6：护盾
     */
    SHIELD(6, "护盾"),

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
    AURA(10, "BUFF/DEBUFF"),

    /**
     * 11：魅惑
     */
    CHARM(11, "魅惑"),

    /**
     * 12：黑暗，失明
     */
    BLIND(12, ""),

    /**
     * 13：沉默
     */
    SILENCE(13, ""),

    /**
     * 14：改变模式
     */
    CHANGE_MODE(14, ""),

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
    TRIGGER(17, ""),

    /**
     * 18：充能
     */
    CHARGE(18, ""),

    /**
     * 19：伤害充能
     */
    DAMAGE_CHARGE(19, ""),

    /**
     * 20：挑衅
     */
    TAUNT(20, "挑衅"),

    /**
     * 21：无敌
     */
    INVINCIBLE(21, "无敌"),

    /**
     * 22：行动模式变更
     */
    CHANGE_PATTERN(22, ""),

    /**
     * 23：判定对象状态
     */
    ACCORD_STATUS(23, ""),

    /**
     * 24：复活
     */
    REVIVAL(24, ""),

    /**
     * 25：连续攻击
     */
    CONTINUOUS_ATTACK(25, ""),

    /**
     * 26：叠加
     */
    ADDITIVE(26, "叠加"),

    /**
     * 27：提升倍率
     */
    MULTIPLE(27, ""),

    /**
     * 28：满足特殊条件：击杀敌人、使用技能后、女仆成功失败等
     */
    IF_TRUE(28, ""),

    /**
     * 29：变更攻击区域？
     */
    CHANGE_SEARCH_AREA(29, ""),

    /**
     * 30：死亡触发
     */
    IF_DIE(30, ""),

    /**
     * 31：连续攻击附近
     */
    CONTINUOUS_ATTACK_NEARBY(31, ""),

    /**
     * 32：吸血效果
     */
    LIFE_STEAL(32, "吸血"),

    /**
     * 33：自动反击
     */
    STRIKE_BACK(33, "格挡"),

    /**
     * 34：伤害递增
     */
    INCREASED_DAMAGE(34, "伤害递增"),

    /**
     * 35：特殊刻印
     */
    SEAL(35, "刻印"),

    /**
     * 36：范围攻击
     */
    ATTACK_FIELD(36, "范围"),

    /**
     * 37：范围治疗
     */
    HEAL_FIELD(37, "范围"),

    /**
     * 38：范围减益
     */
    DEBUFF_FIELD(38, "范围"),

    /**
     * 39：范围持续伤害
     */
    DOT_FIELD(39, "范围"),

    /**
     * 40：范围行动速度变更
     */
    CHANGE_ACTION_SPEED_FIELD(40, ""),

    /**
     * 41：改变 UB 时间
     */
    CHANGE_UB_TIME(41, ""),

    /**
     * 42：循环触发：哈哈剑大笑时...等状态触发
     */
    LOOP_TRIGGER(42, ""),

    /**
     * 43：拥有标记时触发
     */
    IF_TARGETED(43, ""),

    /**
     * 44：每场战斗开始时
     */
    WAVE_START(44, "进场"),

    /**
     * 45：已使用技能数
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
    HOT(48, ""),

    /**
     * 49：移除增益
     */
    DISPEL(49, "净化"),

    /**
     * 50：特殊状态：铃声响起时
     */
    CHANNEL(50, "摇铃"),

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
    STEALTH(54, ""),

    /**
     * 55：部位移动
     */
    MOVE_PART(55, ""),

    /**
     * 56：次数失明 如：拉姆千里眼，攻击一次失效
     */
    COUNT_BLIND(56, ""),

    /**
     * 57：延迟攻击 如：万圣炸弹人的 UB
     */
    COUNT_DOWN(57, "延时"),

    /**
     * 58：解除领域 如：晶姐 UB
     */
    STOP_FIELD(58, ""),

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
    FEAR(61, ""),

    /**
     * 71：特殊状态：公主佩可 UB 后不死BUFF
     */
    PR_PEKO_UB(71, ""),

    /**
     * 75：依据攻击次数增伤：水流夏
     */
    HIT_COUNT(75, ""),

    /**
     * 77：特殊刻印：增益时叠加 圣诞哈哈剑
     */
    IF_BUFF_SEAL(77, ""),

    /**
     * 90：EX被动
     */
    EX(90, "被动"),

    /**
     * 91：EX被动+
     */
    EX_PLUS(91, "被动");

}

fun parse(value: Int): String {
    for (item in SkillActionType.values()) {
        if (item.type == value) return item.desc
    }
    return ""
}


