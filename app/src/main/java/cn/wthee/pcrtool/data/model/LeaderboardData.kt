package cn.wthee.pcrtool.data.model


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
