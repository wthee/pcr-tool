package cn.wthee.pcrtool.data.model


/**
 * 排行详情
 */
data class LeaderboardData(
    val icon: String = "",
    val name: String = "???",
    val url: String = "",
    val quest: String = "?",
    val questScore: Int = 0,
    val tower: String = "?",
    val towerScore: Int = 0,
    val pvp: String = "?",
    val pvpScore: Int = 0,
    val clan: String = "?",
    val clanScore: Int = 0,
    val updateTime: String = "",
    val wikiTime: String = "",
    val unitId: Int? = 0,
) {
    fun getTime() = try {
        wikiTime.substring(0, 11)
    } catch (_: Exception) {
        wikiTime
    }
}
