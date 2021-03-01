package cn.wthee.pcrtool.data.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * 物品信息
 */
@Entity(tableName = "item_data")
data class ItemDataJP(
    @PrimaryKey
    @ColumnInfo(name = "item_id") var item_id: Int = 0,
    @ColumnInfo(name = "item_name") var item_name: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "promotion_level") var promotion_level: Int = 0,
    @ColumnInfo(name = "item_type") var item_type: Int = 0,
    @ColumnInfo(name = "value") var value: Int = 0,
    @ColumnInfo(name = "price") var price: Int = 0,
    @ColumnInfo(name = "limit_num") var limit_num: Int = 0,
    @ColumnInfo(name = "start_time") var start_time: String = "",
    @ColumnInfo(name = "end_time") var end_time: String = "",
    //jp
    @ColumnInfo(name = "gojuon_order") var gojuon_order: Int = 0,
    @ColumnInfo(name = "sell_check_disp") var sell_check_disp: Int = 0,
) : Serializable