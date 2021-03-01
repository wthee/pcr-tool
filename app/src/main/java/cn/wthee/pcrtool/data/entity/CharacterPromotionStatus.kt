package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import cn.wthee.pcrtool.data.view.Attr

/**
 * 角色 Rank 属性状态
 */
@Entity(
    tableName = "unit_promotion_status",
    primaryKeys = ["unit_id", "promotion_level"]
)
data class CharacterPromotionStatus(
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @ColumnInfo(name = "promotion_level") val promotionLevel: Int,
    @Embedded val attr: Attr,
)