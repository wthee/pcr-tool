package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import cn.wthee.pcrtool.data.db.view.Attr


/**
 * 角色 Rank 奖励
 */
@Entity(
    tableName = "promotion_bonus",
    indices = [Index(
        value = arrayOf("unit_id"),
        unique = false,
        name = "promotion_bonus_0_unit_id"
    )],
    primaryKeys = ["unit_id", "promotion_level"]
)
data class UnitPromotionBonus(
    @ColumnInfo(name = "unit_id") val unitId: Int = 0,
    @ColumnInfo(name = "promotion_level") val promotionLevel: Int = 0,
    @Embedded val attr: Attr = Attr(),
)