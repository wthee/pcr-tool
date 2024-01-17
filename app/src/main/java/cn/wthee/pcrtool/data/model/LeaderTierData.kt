package cn.wthee.pcrtool.data.model

import kotlinx.serialization.Serializable

/**
 * 角色评级
 */
@Serializable
data class LeaderTierData(
    val desc: String,
    val leader: List<LeaderTierItem>,
    val tierSummary: List<TierSummary>
)

@Serializable
data class LeaderTierItem(
    val icon: String = "",
    val name: String = "",
    val tier: String = "-1",
    val type: Int = 0,
    val unitId: Int? = 0,
    val url: String = "",
)

@Serializable
data class TierSummary(
    val desc: String,
    val tier: String
)

data class LeaderTierGroup(
    val tier: String,
    val leaderList: ArrayList<LeaderTierItem>,
    val desc: String
)