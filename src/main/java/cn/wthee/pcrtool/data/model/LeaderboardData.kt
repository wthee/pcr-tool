package cn.wthee.pcrtool.data.model

data class LeaderboardData(
    val `data`: List<LeaderboardInfo>,
    val status: Int
)

data class LeaderboardInfo(
    val icon: String,
    val name: String,
    val url: String,
    val all: String,
    val pvp: String,
    val clan: String,
    val tower: String,
)
