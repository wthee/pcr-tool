package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.db.view.ExtraEquipmentBasicInfo

/**
 * ex装备分组信息
 */
data class ExtraEquipGroupData(
    var rarity: Int = 0,
    var categoryName: String = "",
    var equipIdList: ArrayList<ExtraEquipmentBasicInfo> = arrayListOf()
)