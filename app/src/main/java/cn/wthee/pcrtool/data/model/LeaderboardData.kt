package cn.wthee.pcrtool.data.model

/**
 * 排行
 */
data class LeaderData(
    val desc: String,
    val leader: List<LeaderboardData>
)

/**
 * 排行详情
 */
data class LeaderboardData(
    val icon: String,
    val name: String,
    val url: String,
    val all: String,
    val pvp: String,
    val clan: String,
    val tower: String,
)
