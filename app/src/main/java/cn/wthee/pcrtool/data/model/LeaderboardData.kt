package cn.wthee.pcrtool.data.model

import kotlinx.serialization.Serializable


/**
 * 排行详情
 */
@Serializable
data class LeaderboardData(
    val unitId: Int? = 0,
    val name: String = "???",
    val url: String = "",
    val pvp: String = "?",
    val pvpScore: Int = 0,
    val clan: String = "?",
    val clanScore: Int = 0,
    val talent: String = "?",
    val talentScore: Int = 0,
    val updateTime: String? = null,
)
