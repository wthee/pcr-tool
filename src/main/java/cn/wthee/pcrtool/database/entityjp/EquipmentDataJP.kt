package cn.wthee.pcrtool.database.entityjp

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.database.view.Attr
import java.io.Serializable

@Entity(tableName = "equipment_data")
class EquipmentDataJP(
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
    //jp
    @ColumnInfo(name = "display_item") val displayItem: Int,
    @ColumnInfo(name = "item_type") val itemType: Int,
) : Serializable {

    fun getDesc() = description.replace("\\n", "")

}


