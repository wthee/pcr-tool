package cn.wthee.pcrtool.data.network.model


data class LeaderData(
    val desc: String,
    val leader: List<LeaderboardData>
)

data class LeaderboardData(
    val icon: String,
    val name: String,
    val url: String,
    val all: String,
    val pvp: String,
    val clan: String,
    val tower: String,
)
