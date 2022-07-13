package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.db.view.EquipmentBasicInfo

data class EquipGroupData(
    var promotionLevel: Int = 0,
    var requireLevel: Int = 0,
    var equipIdList: ArrayList<EquipmentBasicInfo> = arrayListOf()
)