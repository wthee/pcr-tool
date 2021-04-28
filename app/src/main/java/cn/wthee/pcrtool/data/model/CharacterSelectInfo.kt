package cn.wthee.pcrtool.data.model

/**
 * 角色可选择数值
 * rank、等级、星级、专武等级
 */
data class CharacterSelectInfo(
    var rank: Int = 2,
    var rarity: Int = 0,
    var level: Int = 100,
    var uniqueEquipLevel: Int = 100,
)