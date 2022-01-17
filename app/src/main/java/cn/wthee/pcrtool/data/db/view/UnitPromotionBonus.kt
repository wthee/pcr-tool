package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Embedded


/**
 * 角色 Rank 奖励
 */
data class UnitPromotionBonus(
    @ColumnInfo(name = "unit_id") val unitId: Int = 0,
    @ColumnInfo(name = "promotion_level") val promotionLevel: Int = 0,
    @Embedded val attr: Attr = Attr(),
)