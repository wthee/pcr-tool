package cn.wthee.pcrtool.data.db.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 卡池信息
 */
@Entity(tableName = "gacha_data")
data class GachaDataJP(
    @PrimaryKey
    @ColumnInfo(name = "gacha_id") var gacha_id: Int = 0,
    @ColumnInfo(name = "gacha_name") var gacha_name: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "gacha_detail") var gacha_detail: Int = 0,
    @ColumnInfo(name = "gacha_cost_type") var gacha_cost_type: Int = 0,
    @ColumnInfo(name = "price") var price: Int = 0,
    @ColumnInfo(name = "free_gacha_type") var free_gacha_type: Int = 0,
    @ColumnInfo(name = "free_gacha_interval_time") var free_gacha_interval_time: Int = 0,
    @ColumnInfo(name = "free_gacha_count") var free_gacha_count: Int = 0,
    @ColumnInfo(name = "discount_price") var discount_price: Int = 0,
    @ColumnInfo(name = "gacha_odds") var gacha_odds: String = "",
    @ColumnInfo(name = "gacha_odds_star2") var gacha_odds_star2: String = "",
    @ColumnInfo(name = "gacha_type") var gacha_type: Int = 0,
    @ColumnInfo(name = "movie_id") var movie_id: Int = 0,
    @ColumnInfo(name = "start_time") var start_time: String = "",
    @ColumnInfo(name = "end_time") var end_time: String = "",
    @ColumnInfo(name = "ticket_id") var ticket_id: Int = 0,
    @ColumnInfo(name = "special_id") var special_id: Int = 0,
    @ColumnInfo(name = "exchange_id") var exchange_id: Int = 0,
    @ColumnInfo(name = "ticket_id_10") var ticket_id_10: Int = 0,
    @ColumnInfo(name = "rarity_odds") var rarity_odds: String = "",
    @ColumnInfo(name = "chara_odds_star1") var chara_odds_star1: String = "",
    @ColumnInfo(name = "chara_odds_star2") var chara_odds_star2: String = "",
    @ColumnInfo(name = "chara_odds_star3") var chara_odds_star3: String = "",
    //jp
    @ColumnInfo(name = "pick_up_chara_text") var pick_up_chara_text: String = "",
    @ColumnInfo(name = "description_2") var description_2: String = "",
    @ColumnInfo(name = "description_sp") var description_sp: String = "",
    @ColumnInfo(name = "parallel_id") var parallel_id: Int = 0,
    @ColumnInfo(name = "pickup_badge") var pickup_badge: Int = 0,
    @ColumnInfo(name = "gacha10_special_odds_star1") var gacha10_special_odds_star1: String = "",
    @ColumnInfo(name = "gacha10_special_odds_star2") var gacha10_special_odds_star2: String = "",
    @ColumnInfo(name = "gacha10_special_odds_star3") var gacha10_special_odds_star3: String = "",
    @ColumnInfo(name = "prizegacha_id") var prizegacha_id: Int = 0,
    @ColumnInfo(name = "gacha_bonus_id") var gacha_bonus_id: Int = 0,
    @ColumnInfo(name = "gacha_times_limit10") var gacha_times_limit10: Int = 0,
)