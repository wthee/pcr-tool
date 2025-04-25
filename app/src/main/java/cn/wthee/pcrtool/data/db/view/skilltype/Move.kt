package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import kotlin.math.abs

// 2：位移
fun SkillActionDetail.move(): String {

    val directionText = getString(
        if (actionValue1 > 0) {
            R.string.skill_forward
        } else {
            R.string.skill_backward
        }
    )
    val positionText = getString(
        if (actionValue1 > 0) {
            R.string.skill_ahead
        } else {
            R.string.skill_rear
        }
    )
    val moveText =
        getString(
            R.string.skill_move,
            getTarget(),
            positionText,
            abs(actionValue1).toInt()
        )
    val returnText = getString(R.string.skill_return)
    val speedText = getString(R.string.skill_move_speed, actionValue2.toInt())
    return when (actionDetail1) {
        //移动后返回
        1 -> moveText + returnText
        //前、后移动后返回
        2 -> directionText + moveText + returnText
        //移动
        3 -> moveText
        //方向
        4, 7 -> directionText + moveText
        //方向、速度
        5 -> moveText + speedText
        6 -> directionText + moveText + speedText
        else -> UNKNOWN
    }
}