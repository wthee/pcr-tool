package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Ignore
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.UNKNOWN_EQUIP_ID

/**
 * 专武最大强化
 */
data class UniqueEquipmentMaxData(
    @ColumnInfo(name = "unit_id") var unitId: Int = 0,
    @ColumnInfo(name = "equipment_id") var equipmentId: Int = UNKNOWN_EQUIP_ID,
    @ColumnInfo(name = "equipment_name") var equipmentName: String = "?",
    @ColumnInfo(name = "description") var description: String = "?",
    @Embedded var attr: Attr = Attr(),
    @Ignore var isTpLimitAction: Boolean = false
) {

    /**
     * 获取装备描述
     */
    fun getDesc() = description.replace("\\n", "")

}