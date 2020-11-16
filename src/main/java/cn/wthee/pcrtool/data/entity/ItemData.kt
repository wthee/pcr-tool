package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "item_data")
data class ItemData(
    @PrimaryKey
    @ColumnInfo(name = "item_id") val item_id: Int,
    @ColumnInfo(name = "item_name") val item_name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "promotion_level") val promotion_level: Int,
    @ColumnInfo(name = "item_type") val item_type: Int,
    @ColumnInfo(name = "value") val value: Int,
    @ColumnInfo(name = "price") val price: Int,
    @ColumnInfo(name = "limit_num") val limit_num: Int,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
) : Serializable