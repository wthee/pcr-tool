package cn.wthee.pcrtool.data.model

import kotlinx.serialization.Serializable

/**
 * 公会战信息
 */
@Serializable
data class ClanBattleProperty(
    var clanBattleId: Int,
    var index: Int,
    var minPhase: Int,
    var maxPhase: Int,
)
