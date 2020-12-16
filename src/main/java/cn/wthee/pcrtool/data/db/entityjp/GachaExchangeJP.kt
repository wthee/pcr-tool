package cn.wthee.pcrtool.data.db.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gacha_exchange_lineup")
data class GachaExchangeJP(
    @PrimaryKey
    @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "exchange_id") var exchange_id: Int = 0,
    @ColumnInfo(name = "unit_id") var unit_id: Int = 0,
    @ColumnInfo(name = "rarity") var rarity: Int = 0,
    //jp
    @ColumnInfo(name = "gacha_bonus_id") var gacha_bonus_id: Int = 0,
)