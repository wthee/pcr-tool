package cn.wthee.pcrtool.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EquipmentAttr
import cn.wthee.pcrtool.utils.Constants.UNKNOW_EQUIP_ID
import java.io.Serializable

@Entity(tableName = "equipment_enhance_data",
    primaryKeys = ["promotion_level", "equipment_enhance_level"])
class EquipmentEnhanceData(
    @ColumnInfo(name = "promotion_level") val promotionLevel: Int,
    @ColumnInfo(name = "equipment_enhance_level") val equipmentEnhanceLevel: Int,
    @ColumnInfo(name = "needed_point") val neededPoint: Int,
    @ColumnInfo(name = "total_point") val totalPoint: Int,
) : Serializable


