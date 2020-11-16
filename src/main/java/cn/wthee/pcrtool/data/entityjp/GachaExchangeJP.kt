package cn.wthee.pcrtool.data.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gacha_exchange_lineup")
data class GachaExchangeJP(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "exchange_id") val exchange_id: Int,
    @ColumnInfo(name = "unit_id") val unit_id: Int,
    @ColumnInfo(name = "rarity") val rarity: Int,
    //jp
    @ColumnInfo(name = "gacha_bonus_id") val gacha_bonus_id: Int,
)