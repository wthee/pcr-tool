package cn.wthee.pcrtool.data.enums

import androidx.compose.ui.graphics.Color
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.colorBlue
import cn.wthee.pcrtool.ui.theme.colorCopper
import cn.wthee.pcrtool.ui.theme.colorCyan
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorOrange
import cn.wthee.pcrtool.ui.theme.colorPink
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorSilver
import cn.wthee.pcrtool.ui.theme.colorYellow

/**
 * 装备品级、RANk 颜色
 * @
 */
enum class RankColor(
    val type: Int,
    val color: Color,
    val typeNameId: Int,
    val startRank: Int,
    val endRank: Int
) {
    UNKNOWN(0, colorGray, R.string.unknown, 0, 0),
    BLUE(1, colorBlue, R.string.color_blue, 1, 1),
    COPPER(2, colorCopper, R.string.color_copper, 2, 3),
    SILVER(3, colorSilver, R.string.color_silver, 4, 6),
    GOLD(4, colorGold, R.string.color_gold, 7, 10),
    PURPLE(5, colorPurple, R.string.color_purple, 11, 17),
    RED(6, colorRed, R.string.color_red, 18, 20),
    GREEN(7, colorGreen, R.string.color_green, 21, 23),
    ORANGE(8, colorOrange, R.string.color_orange, 24, 27),
    CYAN(9, colorCyan, R.string.color_cyan, 28, 31),
    PINK(10, colorPink, R.string.color_pink, 32, 39),
    YELLOW(11, colorYellow, R.string.color_yellow, 40, 99),
    ;

    companion object {
        fun getByType(type: Int) = RankColor.entries
            .find { it.type == type } ?: UNKNOWN


        /**
         * rank 颜色
         * @param rank rank数值
         */
        fun getRankColor(rank: Int): Color {
            RankColor.entries.forEach {
                if (rank in it.startRank..it.endRank) {
                    return it.color
                }
            }
            return UNKNOWN.color
        }
    }
}