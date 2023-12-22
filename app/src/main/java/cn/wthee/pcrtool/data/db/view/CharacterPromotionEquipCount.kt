package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 * 角色rank提升装备数量
 */
data class CharacterPromotionEquipCount(
    @ColumnInfo(name = "equip_id") var equipId: Int,
    @ColumnInfo(name = "equip_count") var equipCount: Int
)