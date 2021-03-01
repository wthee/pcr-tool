package cn.wthee.pcrtool.data.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 卡池交换角色信息
 */
@Entity(
    tableName = "gacha_exchange_lineup",
    indices = [Index(
        value = arrayOf("exchange_id"),
        unique = false,
        name = "gacha_exchange_lineup_0_exchange_id"
    )]
)
data class GachaExchangeJP(
    @PrimaryKey
    @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "exchange_id") var exchange_id: Int = 0,
    @ColumnInfo(name = "unit_id") var unit_id: Int = 0,
    @ColumnInfo(name = "rarity") var rarity: Int = 0,
    //jp
    @ColumnInfo(name = "gacha_bonus_id") var gacha_bonus_id: Int = 0,
    @ColumnInfo(name = "start_time") var start_time: String = "",
    @ColumnInfo(name = "end_time") var end_time: String = "",
)