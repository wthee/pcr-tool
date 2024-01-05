package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

/**
 * 卡池角色信息
 */

@Serializable
data class GachaUnitInfo(
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @ColumnInfo(name = "unit_name") val unitName: String,
    @ColumnInfo(name = "is_limited") val isLimited: Int,
    @ColumnInfo(name = "rarity") val rarity: Int,
)