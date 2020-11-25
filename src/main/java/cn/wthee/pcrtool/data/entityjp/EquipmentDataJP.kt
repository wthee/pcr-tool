package cn.wthee.pcrtool.data.entityjp

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.data.view.Attr
import java.io.Serializable

@Entity(tableName = "equipment_data")
class EquipmentDataJP(
    @PrimaryKey
    @ColumnInfo(name = "equipment_id") var equipmentId: Int = 0,
    @ColumnInfo(name = "equipment_name") var equipmentName: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "promotion_level") var promotionLevel: Int = 0,
    @ColumnInfo(name = "craft_flg") var craftFlg: Int = 0,
    @ColumnInfo(name = "equipment_enhance_point") var equipmentEnhancePoint: Int = 0,
    @ColumnInfo(name = "sale_price") var salePrice: Int = 0,
    @ColumnInfo(name = "require_level") var requireLevel: Int = 0,
    @ColumnInfo(name = "enable_donation") var enableDonation: Int = 0,
    @Embedded var attr: Attr = Attr(),
    //jp
    @ColumnInfo(name = "display_item") var displayItem: Int = 0,
    @ColumnInfo(name = "item_type") var itemType: Int = 0,
) : Serializable


