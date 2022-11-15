package cn.wthee.pcrtool.data.model

/**
 * 排行
 */
data class LeaderData(
    val desc: String = "",
    var leader: List<LeaderboardData>? = null
)

/**
 * 排行详情
 */
data class LeaderboardData(
    val icon: String = "",
    val name: String = "???",
    val url: String = "",
    val quest: String = "?",
    val tower: String = "?",
    val pvp: String = "?",
    val clan: String = "?",
    val questFlag: Int = 0,
    val towerFlag: Int = 0,
    val pvpFlag: Int = 0,
    val clanFlag: Int = 0,
    val isNew: Int = 0,
    val orderId: Int = 0,
)
