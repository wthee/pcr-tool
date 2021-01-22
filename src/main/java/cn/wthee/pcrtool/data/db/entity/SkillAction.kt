package cn.wthee.pcrtool.data.db.entity

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import kotlin.math.abs

/**
 * 技能效果
 */
@Entity(tableName = "skill_action")
data class SkillAction(
    @PrimaryKey
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
    @ColumnInfo(name = "level_up_disp") val level_up_disp: String
) {

    /**
     * 获取技能效果
     *
     */
    fun getFixedDesc(): SpannableStringBuilder {
        val fixed: String
        val c = MyApplication.context

        /**
         * 技能施放对象
         * [target_assignment]
         * 0：自身 1：敌方 2：自身或己方
         *
         * 技能施放方向
         * [target_area]
         * 1：前方 2：前后 3：全体
         *
         * 技能施放对象数量
         * [target_count]
         * 0,1,2,3 99：范围内全体
         *
         * 技能范围
         * [target_range]
         * > 2160: 全体
         * <2160 ：范围
         */
        val desc = when (action_type) {
            //伤害, 每秒恢复
            1, 37, 48 -> "<$action_value_1 + $action_value_2 * 技能等级 + $action_value_3 * 攻击力>"
            //位置变动
            2 -> "移动至最近敌人前$action_value_1，移动速度$action_value_2"
            //击退/击飞/拉近
            3 -> {
                if (action_value_4 != 0.toDouble()) {
                    "击飞距离<$action_value_1>"
                } else {
                    if (action_value_1 > 0)
                        "击退距离<$action_value_1>"
                    else
                        "拉近距离<${abs(action_value_1)}>"
                }
            }
            //恢复
            4 -> "<$action_value_2 + $action_value_3 * 技能等级 + $action_value_4 * 攻击力>"
            //护盾，持续伤害
            6, 9 -> "<$action_value_1 + $action_value_2 * 技能等级>，持续<$action_value_3>秒"
            //视点切换
//            7 -> "自身攻击目标切换"
            //速度变化/限制行动
            8 -> {
                if (action_value_1 != 0.toDouble()) {
                    "，速度 * <$action_value_1>， 持续<$action_value_3>秒"
                } else {
                    "，持续<$action_value_3>秒"
                }
            }
            //buff/debuff
            10 -> {
                if (action_value_3 != 0.toDouble()) {
                    "<$action_value_2 + $action_value_3 * 技能等级>，持续<$action_value_4>秒"
                } else {
                    "<$action_value_2>，持续<$action_value_4>秒"
                }

            }
            //持续伤害
            11, 12 -> "，持续<$action_value_1>秒，" + if (action_value_3 == 100.toDouble()) "成功率100%" else "成功率<1 + $action_value_3 * 技能等级 %>"
            //TP变化
            14 -> {
                if (action_value_1 != 0.toDouble()) {
                    "每秒降低TP <$action_value_1>"
                } else {
                    ""
                }
            }
            16 -> {
                if (action_value_2 == 0.toDouble()) {
                    "<$action_value_1>"
                } else {
                    "<$action_value_1 + $action_value_2 * 技能等级>"
                }
            }
            //挑衅 , 无敌
            20, 21 -> {
                if (action_value_2 == 0.toDouble()) {
                    ", 持续<$action_value_1>秒"
                } else {
                    ", 持续<$action_value_1 + $action_value_2 * 技能等级>秒"
                }
            }
            33 -> "<$action_value_1 + $action_value_2 * 技能等级>"
            34 -> "<$action_value_2 + $action_value_3 * 技能等级>, 叠加上限<$action_value_4>"
            //领域
            38 -> if (action_value_2 == 0.toDouble()) {
                "$action_value_1 ，持续<$action_value_3>秒"
            } else {
                "<$action_value_1 + $action_value_2 * 技能等级>，持续<$action_value_3>秒"
            }
            50 -> "<$action_value_2 + $action_value_3 * 技能等级>，持续<$action_value_4>秒"
            //提升攻击力等
            90 -> if (action_value_3 == 0.toDouble()) {
                "<$action_value_2>"
            } else {
                "<$action_value_2 + $action_value_3 * 技能等级>"
            }
            else -> ""
        }
        fixed = when {
            description.contains("0") -> description.replace("{0}", desc)
            else -> description.plus(desc)
        }
        val spannable = SpannableStringBuilder(fixed)
        val start0 = fixed.indexOfFirst { it == '<' }
        val start1 = fixed.indexOfLast { it == '<' }
        val end0 = fixed.indexOfFirst { it == '>' }
        val end1 = fixed.indexOfLast { it == '>' }
        if (start0 != -1 && end0 != -1) {
            spannable.setSpan(
                ForegroundColorSpan(
                    MyApplication.context.getColor(R.color.colorPrimary)
                ), start0, end0 + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        if (start1 != -1 && end1 != -1) {
            spannable.setSpan(
                ForegroundColorSpan(
                    MyApplication.context.getColor(R.color.colorPrimary)
                ), start1, end1 + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }
}