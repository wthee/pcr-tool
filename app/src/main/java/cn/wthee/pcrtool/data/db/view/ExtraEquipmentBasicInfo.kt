package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.UNKNOWN_EQUIP_ID

/**
 * ex装备基础信息
 */
data class ExtraEquipmentBasicInfo(
    @ColumnInfo(name = "ex_equipment_id") var equipmentId: Int = UNKNOWN_EQUIP_ID,
    @ColumnInfo(name = "name") var equipmentName: String = "",
    @ColumnInfo(name = "clan_battle_equip_flag") var clanFlag: Int = 0,
    @ColumnInfo(name = "rarity") var rarity: Int = 0,
    @ColumnInfo(name = "category") var category: Int = 0,
    @ColumnInfo(name = "category_name") var categoryName: String = "",
)

