package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.data.db.view.skilltype.accumulativeDamage
import cn.wthee.pcrtool.data.db.view.skilltype.actionDot
import cn.wthee.pcrtool.data.db.view.skilltype.attackField
import cn.wthee.pcrtool.data.db.view.skilltype.attackSeal
import cn.wthee.pcrtool.data.db.view.skilltype.aura
import cn.wthee.pcrtool.data.db.view.skilltype.auraField
import cn.wthee.pcrtool.data.db.view.skilltype.awe
import cn.wthee.pcrtool.data.db.view.skilltype.barrier
import cn.wthee.pcrtool.data.db.view.skilltype.cannotSelected
import cn.wthee.pcrtool.data.db.view.skilltype.changeMode
import cn.wthee.pcrtool.data.db.view.skilltype.changePattern
import cn.wthee.pcrtool.data.db.view.skilltype.changePosition
import cn.wthee.pcrtool.data.db.view.skilltype.changeTpRatio
import cn.wthee.pcrtool.data.db.view.skilltype.changeWidth
import cn.wthee.pcrtool.data.db.view.skilltype.channel
import cn.wthee.pcrtool.data.db.view.skilltype.charge
import cn.wthee.pcrtool.data.db.view.skilltype.charm
import cn.wthee.pcrtool.data.db.view.skilltype.chooseEnemy
import cn.wthee.pcrtool.data.db.view.skilltype.coefficient
import cn.wthee.pcrtool.data.db.view.skilltype.copyAtk
import cn.wthee.pcrtool.data.db.view.skilltype.countBlind
import cn.wthee.pcrtool.data.db.view.skilltype.countDown
import cn.wthee.pcrtool.data.db.view.skilltype.damage
import cn.wthee.pcrtool.data.db.view.skilltype.damageReduce
import cn.wthee.pcrtool.data.db.view.skilltype.damageTakenUp
import cn.wthee.pcrtool.data.db.view.skilltype.dispel
import cn.wthee.pcrtool.data.db.view.skilltype.dot
import cn.wthee.pcrtool.data.db.view.skilltype.dotField
import cn.wthee.pcrtool.data.db.view.skilltype.dotUp
import cn.wthee.pcrtool.data.db.view.skilltype.environment
import cn.wthee.pcrtool.data.db.view.skilltype.ex
import cn.wthee.pcrtool.data.db.view.skilltype.exEquipFull
import cn.wthee.pcrtool.data.db.view.skilltype.exEquipHalf
import cn.wthee.pcrtool.data.db.view.skilltype.exemptionDeath
import cn.wthee.pcrtool.data.db.view.skilltype.fear
import cn.wthee.pcrtool.data.db.view.skilltype.guard
import cn.wthee.pcrtool.data.db.view.skilltype.heal
import cn.wthee.pcrtool.data.db.view.skilltype.healDown
import cn.wthee.pcrtool.data.db.view.skilltype.healField
import cn.wthee.pcrtool.data.db.view.skilltype.hide
import cn.wthee.pcrtool.data.db.view.skilltype.hitCount
import cn.wthee.pcrtool.data.db.view.skilltype.hot
import cn.wthee.pcrtool.data.db.view.skilltype.ifBuffSeal
import cn.wthee.pcrtool.data.db.view.skilltype.ifHasField
import cn.wthee.pcrtool.data.db.view.skilltype.ifSpStatus
import cn.wthee.pcrtool.data.db.view.skilltype.ifStatus
import cn.wthee.pcrtool.data.db.view.skilltype.ignoreSpeedDown
import cn.wthee.pcrtool.data.db.view.skilltype.ignoreTaunt
import cn.wthee.pcrtool.data.db.view.skilltype.inhibitHeal
import cn.wthee.pcrtool.data.db.view.skilltype.invincible
import cn.wthee.pcrtool.data.db.view.skilltype.killMe
import cn.wthee.pcrtool.data.db.view.skilltype.lifeSteal
import cn.wthee.pcrtool.data.db.view.skilltype.limitAttack
import cn.wthee.pcrtool.data.db.view.skilltype.logBarrier
import cn.wthee.pcrtool.data.db.view.skilltype.loop
import cn.wthee.pcrtool.data.db.view.skilltype.loopTrigger
import cn.wthee.pcrtool.data.db.view.skilltype.magicChange
import cn.wthee.pcrtool.data.db.view.skilltype.magicChangeReduceDamage
import cn.wthee.pcrtool.data.db.view.skilltype.move
import cn.wthee.pcrtool.data.db.view.skilltype.movePart
import cn.wthee.pcrtool.data.db.view.skilltype.noTarget
import cn.wthee.pcrtool.data.db.view.skilltype.noUB
import cn.wthee.pcrtool.data.db.view.skilltype.persistent
import cn.wthee.pcrtool.data.db.view.skilltype.rateDamage
import cn.wthee.pcrtool.data.db.view.skilltype.reindeer
import cn.wthee.pcrtool.data.db.view.skilltype.revival
import cn.wthee.pcrtool.data.db.view.skilltype.seal
import cn.wthee.pcrtool.data.db.view.skilltype.sealCount
import cn.wthee.pcrtool.data.db.view.skilltype.sealV2
import cn.wthee.pcrtool.data.db.view.skilltype.skillCount
import cn.wthee.pcrtool.data.db.view.skilltype.specialEffect
import cn.wthee.pcrtool.data.db.view.skilltype.speed
import cn.wthee.pcrtool.data.db.view.skilltype.stealth
import cn.wthee.pcrtool.data.db.view.skilltype.stopField
import cn.wthee.pcrtool.data.db.view.skilltype.strikeBack
import cn.wthee.pcrtool.data.db.view.skilltype.sumCritical
import cn.wthee.pcrtool.data.db.view.skilltype.summon
import cn.wthee.pcrtool.data.db.view.skilltype.taunt
import cn.wthee.pcrtool.data.db.view.skilltype.tp
import cn.wthee.pcrtool.data.db.view.skilltype.tpField
import cn.wthee.pcrtool.data.db.view.skilltype.tpHit
import cn.wthee.pcrtool.data.db.view.skilltype.tpHitReduce
import cn.wthee.pcrtool.data.db.view.skilltype.transferDamage
import cn.wthee.pcrtool.data.db.view.skilltype.trigger
import cn.wthee.pcrtool.data.db.view.skilltype.triggerV2
import cn.wthee.pcrtool.data.db.view.skilltype.unknownType
import cn.wthee.pcrtool.data.db.view.skilltype.waveStart
import cn.wthee.pcrtool.data.enums.SkillActionType
import cn.wthee.pcrtool.data.model.SkillActionText
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.getString


/**
 * 技能效果 fixme 优化（拆分）代码
 */
@Suppress("RegExpRedundantEscape")
data class SkillActionDetail(
    @PrimaryKey
    @ColumnInfo(name = "lv") var level: Int = 0,
    @ColumnInfo(name = "atk") var atk: Int = 0,
    @ColumnInfo(name = "action_id") var actionId: Int = 0,
    @ColumnInfo(name = "class_id") var classId: Int = 0,
    @ColumnInfo(name = "action_type") var actionType: Int = 0,
    @ColumnInfo(name = "action_detail_1") var actionDetail1: Int = 0,
    @ColumnInfo(name = "action_detail_2") var actionDetail2: Int = 0,
    @ColumnInfo(name = "action_detail_3") var actionDetail3: Int = 0,
    @ColumnInfo(name = "action_value_1") var actionValue1: Double = 0.0,
    @ColumnInfo(name = "action_value_2") var actionValue2: Double = 0.0,
    @ColumnInfo(name = "action_value_3") var actionValue3: Double = 0.0,
    @ColumnInfo(name = "action_value_4") var actionValue4: Double = 0.0,
    @ColumnInfo(name = "action_value_5") var actionValue5: Double = 0.0,
    @ColumnInfo(name = "action_value_6") var actionValue6: Double = 0.0,
    @ColumnInfo(name = "action_value_7") var actionValue7: Double = 0.0,
    @ColumnInfo(name = "target_assignment") var targetAssignment: Int = 0,
    @ColumnInfo(name = "target_area") var targetArea: Int = 0,
    @ColumnInfo(name = "target_range") var targetRange: Int = 0,
    @ColumnInfo(name = "target_type") var targetType: Int = 0,
    @ColumnInfo(name = "target_number") var targetNumber: Int = 0,
    @ColumnInfo(name = "target_count") var targetCount: Int = 0,
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "ailment_name") var tag: String = "",
    @ColumnInfo(name = "isRfSkill") var isRfSkill: Boolean = false,
    @ColumnInfo(name = "isOtherRfSkill") var isOtherRfSkill: Boolean = false,
    @Ignore
    var dependId: Int = 0,
    @Ignore
    var isTpLimitAction: Boolean = false,
    @Ignore
    var isOtherLimitAction: Boolean = false
) {

    /**
     * 获取技能效果
     * 判断逻辑参考  MalitsPlus [https://github.com/MalitsPlus]
     */
    fun getActionDesc(): SkillActionText {
        //召唤物编号
        var summonUnitId = 0
        if (SkillActionType.getByType(actionType) == SkillActionType.SUMMON) {
            summonUnitId = actionDetail2
        }
        //生成技能效果文本
        val formatDescText = try {
            formatDesc()
        } catch (e: Exception) {
            LogReportUtil.upload(e, getDebugText())
            UNKNOWN
        }
        //是否显示系数判断
        val showCoe = when (SkillActionType.getByType(actionType)) {
            SkillActionType.ADDITIVE,
            SkillActionType.MULTIPLE,
            SkillActionType.DIVIDE,
            SkillActionType.RATE_DAMAGE -> true

            else -> false
        }
        val skillActionText = SkillActionText(
            actionId = actionId,
            tag = tag,
            actionDesc = "(${actionId % 100}) $formatDescText",
            summonUnitId = summonUnitId,
            showCoe = showCoe,
            level = level,
            atk = atk,
            isTpLimitAction = isTpLimitAction,
            isOtherLimitAction = isOtherLimitAction
        )
        if (BuildConfig.DEBUG) {
            skillActionText.debugText = getDebugText()
        }
        return skillActionText
    }

    /**
     * 调试用信息
     */
    private fun getDebugText() = """
                    action_id:${this.actionId}
                    action_type:${this.actionType}
                    detail:${this.actionDetail1}/${this.actionDetail2}/${this.actionDetail3}
                    value:${this.actionValue1}/${this.actionValue2}/${this.actionValue3}/${this.actionValue4}
                    /${this.actionValue5}/${this.actionValue6}/${this.actionValue7}
                    targetAssignment:${this.targetAssignment}
                    targetCount:${this.targetCount}
                    targetNumber:${this.targetNumber}
                    targetRange:${this.targetRange}
                    targetType:${this.targetType}
                    targetArea:${this.targetArea}
                """.trimIndent()

    /**
     * 获取描述详情
     */
    private fun formatDesc(): String {
        //设置状态标签
        val ailmentName = getString(SkillActionType.getByType(actionType).descId)
        if (ailmentName.isNotEmpty()) {
            tag = ailmentName
        }

        return when (SkillActionType.getByType(actionType)) {
            SkillActionType.DAMAGE -> damage()
            // 2：位移
            SkillActionType.MOVE -> move()
            // 3：改变对方位置
            SkillActionType.CHANGE_ENEMY_POSITION -> changePosition()
            // 4：回复 HP
            SkillActionType.HEAL -> heal()
            // 5：回复 HP
            SkillActionType.CURE -> UNKNOWN
            // 6：护盾
            SkillActionType.BARRIER -> barrier()
            // 7：指定攻击对象
            SkillActionType.CHOOSE_ENEMY -> chooseEnemy()
            // 8：行动速度变更、83：可叠加行动速度变更、99：范围速度变更
            SkillActionType.CHANGE_ACTION_SPEED,
            SkillActionType.SUPERIMPOSE_CHANGE_ACTION_SPEED,
            SkillActionType.SPEED_FIELD -> speed()
            // 9：持续伤害
            SkillActionType.DOT -> dot()
            // 10：buff/debuff
            SkillActionType.AURA, SkillActionType.AURA_V2 -> aura()
            //11：魅惑/混乱12：黑暗 13：沉默
            SkillActionType.CHARM,
            SkillActionType.BLIND,
            SkillActionType.SILENCE -> charm()
            // 14：行动模式变更
            SkillActionType.CHANGE_MODE -> changeMode()
            // 15：召唤
            SkillActionType.SUMMON -> summon()
            // 16：TP 相关
            SkillActionType.CHANGE_TP -> tp()
            // 17：触发条件
            SkillActionType.TRIGGER -> trigger()
            //111：触发条件？??
            SkillActionType.TRIGGER_V2 -> triggerV2()
            // 18：蓄力、19：伤害充能
            SkillActionType.CHARGE, SkillActionType.DAMAGE_CHARGE -> charge()
            // 20：挑衅
            SkillActionType.TAUNT -> taunt(ailmentName)
            // 21：回避
            SkillActionType.INVINCIBLE -> invincible()
            // 22：循环变更
            SkillActionType.CHANGE_PATTERN -> changePattern()
            // 23：判定对象状态
            SkillActionType.IF_STATUS -> ifStatus()
            // 24：复活
            SkillActionType.REVIVAL -> revival()
            // 25：连续攻击
            SkillActionType.CONTINUOUS_ATTACK -> UNKNOWN
            // 26：系数提升
            SkillActionType.ADDITIVE,
            SkillActionType.MULTIPLE,
            SkillActionType.DIVIDE -> coefficient()
            // 28：特殊条件
            SkillActionType.IF_SP_STATUS -> ifSpStatus()
            // 29：无法使用 UB
            SkillActionType.NO_UB -> noUB()
            // 30：立即死亡
            SkillActionType.KILL_ME -> killMe()
            SkillActionType.CONTINUOUS_ATTACK_NEARBY -> UNKNOWN
            // 32：HP吸收
            SkillActionType.LIFE_STEAL -> lifeSteal(ailmentName)
            // 33：反伤
            SkillActionType.STRIKE_BACK -> strikeBack()
            // 34、102：伤害递增
            SkillActionType.ACCUMULATIVE_DAMAGE,
            SkillActionType.ACCUMULATIVE_DAMAGE_V2 -> accumulativeDamage()
            // 35：特殊标记
            SkillActionType.SEAL -> seal()
            // 101：特殊标记v2
            SkillActionType.SEAL_V2 -> sealV2()
            // 36：攻击领域展开
            SkillActionType.ATTACK_FIELD -> attackField()
            // 37：治疗领域展开
            SkillActionType.HEAL_FIELD -> healField()
            // 38：buff/debuff领域展开
            SkillActionType.AURA_FIELD -> auraField()
            // 39：持续伤害领域展开
            SkillActionType.DOT_FIELD -> dotField()

            SkillActionType.CHANGE_ACTION_SPEED_FIELD -> UNKNOWN
            SkillActionType.CHANGE_UB_TIME -> UNKNOWN

            // 42：触发
            SkillActionType.LOOP_TRIGGER -> loopTrigger()
            // 43：拥有标记时触发
            SkillActionType.IF_TARGETED -> UNKNOWN
            // 44：进场等待
            SkillActionType.WAVE_START -> waveStart()
            // 45：已使用技能数相关
            SkillActionType.SKILL_COUNT -> skillCount()
            // 46：比例伤害
            SkillActionType.RATE_DAMAGE -> rateDamage()
            // 47：上限伤害
            SkillActionType.UPPER_LIMIT_ATTACK -> limitAttack()
            // 48：持续治疗
            SkillActionType.HOT -> hot()
            // 49：移除增益
            SkillActionType.DISPEL -> dispel()
            // 50：持续动作
            SkillActionType.CHANNEL -> channel()
            // 52：改变单位距离
            SkillActionType.CHANGE_WIDTH -> changeWidth()
            // 53：特殊状态：领域存在时；如：情姐
            SkillActionType.IF_HAS_FIELD -> ifHasField()
            // 54：隐身
            SkillActionType.STEALTH -> stealth()
            // 55：部位移动
            SkillActionType.MOVE_PART -> movePart()
            // 56：千里眼
            SkillActionType.COUNT_BLIND -> countBlind()
            // 57：延迟攻击 如：万圣炸弹人的 UB
            SkillActionType.COUNT_DOWN -> countDown()
            // 58：解除领域 如：晶姐 UB
            SkillActionType.STOP_FIELD -> stopField()
            // 59：回复妨碍
            SkillActionType.INHIBIT_HEAL_ACTION -> inhibitHeal()
            // 60：标记赋予
            SkillActionType.ATTACK_SEAL -> attackSeal()
            // 61：恐慌
            SkillActionType.FEAR -> fear(ailmentName)
            // 62：畏惧
            SkillActionType.AWE -> awe()
            // 63: 循环动作
            SkillActionType.LOOP -> loop()
            // 69：变身
            SkillActionType.REINDEER -> reindeer()
            // 71：免死
            SkillActionType.EXEMPTION_DEATH -> exemptionDeath()
            // 72：伤害减免
            SkillActionType.DAMAGE_REDUCE -> damageReduce()
            // 73：伤害护盾
            SkillActionType.LOG_BARRIER -> logBarrier()
            // 75：次数触发
            SkillActionType.HIT_COUNT -> hitCount()
            // 76：HP 回复量变化
            SkillActionType.HEAL_DOWN -> healDown()
            // 77：被动叠加标记
            SkillActionType.IF_BUFF_SEAL -> ifBuffSeal()
            // 78：被击伤害上升
            SkillActionType.DMG_TAKEN_UP -> damageTakenUp()
            // 79：行动时，造成伤害
            SkillActionType.ACTION_DOT -> actionDot()
            // 81：无效目标
            SkillActionType.NO_TARGET -> noTarget()
            // 90：EX被动
            SkillActionType.EX -> ex()
            // 901：战斗开始时生效
            SkillActionType.EX_EQUIP -> exEquipFull()
            // 902：45秒
            SkillActionType.EX_EQUIP_HALF -> exEquipHalf()
            // 92：改变 TP 获取倍率
            SkillActionType.CHANGE_TP_RATIO -> changeTpRatio()
            // 93：无视挑衅
            SkillActionType.IGNORE_TAUNT -> ignoreTaunt()
            // 94：技能特效
            SkillActionType.SPECIAL_EFFECT -> specialEffect()
            // 95：隐匿
            SkillActionType.HIDE -> hide()
            // 96：范围tp回复
            SkillActionType.TP_FIELD -> tpField()
            // 97：受击tp回复
            SkillActionType.TP_HIT -> tpHit()
            // 98：改变 TP 减少时倍率
            SkillActionType.TP_HIT_REDUCE -> tpHitReduce()
            // 100：免疫无法行动的异常状态
            SkillActionType.IGNORE_SPEED_DOWN -> ignoreSpeedDown()
            // 103：复制攻击力
            SkillActionType.COPY_ATK -> copyAtk()
            // 105：环境效果
            SkillActionType.ENVIRONMENT -> environment()
            // 106：守护
            SkillActionType.GUARD -> guard()
            // 107：暴击率合计
            SkillActionType.SUM_CRITICAL -> sumCritical()
            // 110：持续伤害易伤
            SkillActionType.DOT_UP -> dotUp()
            // 114：特殊标记计数？
            SkillActionType.SEAL_COUNT -> sealCount()
            // 116：执着状态
            SkillActionType.PERSISTENT -> persistent()
            // 121：幻化状态
            SkillActionType.MAGIC_CHANGE -> magicChange()
            // 123：减伤状态
            SkillActionType.MAGIC_CHANGE_REDUCE_DAMAGE -> magicChangeReduceDamage()
            // 124：护盾（转移伤害）
            SkillActionType.TRANSFER_DAMAGE -> transferDamage()
            // 125：无法选中
            SkillActionType.CANNOT_SELECTED -> cannotSelected()
            else -> unknownType()
        }
    }


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
        false,
        false,
        0,
        false
    )
}