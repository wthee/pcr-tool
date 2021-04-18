package cn.wthee.pcrtool.data.model

/**
 * 角色可选择数值
 * rank、等级、星级、专武等级
 */
data class CharacterSelectInfo(
    val rank: Int,
    val rarity: Int,
    val level: Int,
    val uniqueEquipLevel: Int,
)