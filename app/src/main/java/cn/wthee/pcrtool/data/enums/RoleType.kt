package cn.wthee.pcrtool.data.enums

import androidx.compose.ui.graphics.Color
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.colorCyan
import cn.wthee.pcrtool.ui.theme.colorDeepBlue
import cn.wthee.pcrtool.ui.theme.colorDeepPink
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorOrange
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed

/**
 * 职能类型
 */
enum class RoleType(val type: Int, val color: Color, val typeNameId: Int) {
    ALL(0, Color.Unspecified, R.string.role),
    ATTACKER(1, colorRed, R.string.attacker),
    BREAKER(2, colorGold, R.string.breaker),
    BUFFER(3, colorOrange, R.string.buffer),
    DEBUFFER(4, colorDeepBlue, R.string.debuffer),
    BOOSTER(5, colorCyan, R.string.booster),
    HEALER(6, colorGreen, R.string.healer),
    TANK(7, colorPurple, R.string.tank),
    JAMMER(8, colorDeepPink, R.string.jammer),
    ;

    companion object {
        fun getByType(type: Int) = entries
            .find { it.type == type } ?: ALL
    }
}