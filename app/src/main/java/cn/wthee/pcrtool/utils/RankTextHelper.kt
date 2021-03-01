package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.R


//rank 颜色
fun getRankColor(rank: Int): Int {
    val color = when (rank) {
        in 2..3 -> R.color.color_rank_2_3
        in 4..6 -> R.color.color_rank_4_6
        in 7..10 -> R.color.color_rank_7_10
        in 11..17 -> R.color.color_rank_11_17
        in 18..99 -> R.color.color_rank_18
        else -> {
            R.color.color_rank_2_3
        }
    }
    return ResourcesUtil.getColor(color)
}

/**
 * Rank 格式化
 */
fun getRankText(rank: Int): String {
    val text = when (rank) {
        in 0..9 -> "  $rank"
        else -> "$rank"

    }
    return "RANK $text"
}