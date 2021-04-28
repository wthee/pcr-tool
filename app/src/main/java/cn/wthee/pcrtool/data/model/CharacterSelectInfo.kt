package cn.wthee.pcrtool.data.model

/**
 * 角色可选择数值
 * rank、等级、星级、专武等级
 */
data class CharacterSelectInfo(
    val rank: Int = 2,
    val rarity: Int = 5,
    val level: Int = 100,
    val uniqueEquipLevel: Int = 100,
)