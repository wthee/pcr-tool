package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Embedded

/**
 * 角色 Rank 属性状态
 */
data class UnitPromotionStatus(
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @ColumnInfo(name = "promotion_level") val promotionLevel: Int,
    @Embedded val attr: Attr,
)