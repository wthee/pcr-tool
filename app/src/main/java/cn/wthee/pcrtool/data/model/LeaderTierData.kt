package cn.wthee.pcrtool.data.model

/**
 * 角色评级
 */
data class LeaderTierData(
    val desc: String,
    val leader: List<LeaderTierItem>,
    val tierSummary: List<TierSummary>
)

data class LeaderTierItem(
    val icon: String,
    val name: String,
    val tier: Int,
    val type: Int,
    val unitId: Int? = 0,
    val url: String,
)

data class TierSummary(
    val desc: String,
    val tier: String
)

data class LeaderTierGroup(
    val tier: Int,
    val leaderList: ArrayList<LeaderTierItem>,
    val desc: String
)