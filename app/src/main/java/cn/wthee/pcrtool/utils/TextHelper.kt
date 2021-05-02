package cn.wthee.pcrtool.utils

import kotlin.math.ceil

/**
 * [Double] 转 [Int]，向上取整
 */
val Double.int: Int
    get() {
        return ceil(this).toInt()
    }


/**
 * Rank 格式化
 */
fun getFormatText(rank: Int, preStr: String = Constants.RANK_UPPER): String {
    val text = when (rank) {
        in 0..9 -> "  $rank"
        else -> "$rank"

    }
    return "$preStr $text"
}

/**
 * 阶段格式化
 */
fun getZhNumberText(section: Int): String {
    return when (section) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        5 -> "五"
        6 -> "六"
        7 -> "七"
        else -> section.toString()

    }
}
