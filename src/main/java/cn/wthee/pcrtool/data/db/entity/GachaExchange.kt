package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 卡池可交换角色信息
 */
@Entity(tableName = "gacha_exchange_lineup")
data class GachaExchange(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "exchange_id") val exchange_id: Int,
    @ColumnInfo(name = "unit_id") val unit_id: Int,
    @ColumnInfo(name = "rarity") val rarity: Int,
)