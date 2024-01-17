package cn.wthee.pcrtool.data.model

import kotlinx.serialization.Serializable

/**
 * 召唤物数值
 */
@Serializable
data class SummonProperty(
    var id: Int = 0,
    var type: Int = 0,
    var level: Int = 0,
    var rank: Int = 0,
    var rarity: Int = 0,
)
