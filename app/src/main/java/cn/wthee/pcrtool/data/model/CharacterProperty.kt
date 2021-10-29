package cn.wthee.pcrtool.data.model

/**
 * 角色等级等数值
 *
 * @param uniqueEquipmentLevel 可为0
 */
data class CharacterProperty(
    var level: Int = 0,
    var rank: Int = 0,
    var rarity: Int = 5,
    var uniqueEquipmentLevel: Int = 0
) {
    fun isInit() = level != 0

    fun update(
        level: Int = this.level,
        rank: Int = this.rank,
        rarity: Int = this.rarity,
        uniqueEquipmentLevel: Int = this.uniqueEquipmentLevel
    ): CharacterProperty {
        return CharacterProperty(level, rank, rarity, uniqueEquipmentLevel)
    }
}
