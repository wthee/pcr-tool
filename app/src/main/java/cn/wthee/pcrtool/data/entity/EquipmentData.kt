package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.data.view.Attr

/**
 * 装备基本信息
 */
@Entity(tableName = "equipment_data")
class EquipmentData(
    @PrimaryKey
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "equipment_name") val equipmentName: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "promotion_level") val promotionLevel: Int,
    @ColumnInfo(name = "craft_flg") val craftFlg: Int,
    @ColumnInfo(name = "equipment_enhance_point") val equipmentEnhancePoint: Int,
    @ColumnInfo(name = "sale_price") val salePrice: Int,
    @ColumnInfo(name = "require_level") val requireLevel: Int,
    @ColumnInfo(name = "enable_donation") val enableDonation: Int,
    @Embedded val attr: Attr,
)

