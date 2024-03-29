package cn.wthee.pcrtool.data.model

import kotlinx.serialization.Serializable

/**
 * 额外装备掉落信息
 * 37章后掉落
 * type：0 全部、1 前半、 2 后半
 */
@Serializable
data class RandomEquipDropArea(
    val area: Int = 0,
    val equipIds: String = "",
    val type: Int = 0
)
