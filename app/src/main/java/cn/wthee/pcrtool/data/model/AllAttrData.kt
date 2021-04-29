package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.view.Attr
import cn.wthee.pcrtool.data.view.EquipmentMaxData
import cn.wthee.pcrtool.data.view.UniqueEquipmentMaxData

/**
 * 角色相关属性
 */
data class AllAttrData(
    var sumAttr: Attr = Attr(),
    var stroyAttr: Attr = Attr(),
    var equips: List<EquipmentMaxData> = arrayListOf(),
    var uniqueEquip: UniqueEquipmentMaxData = UniqueEquipmentMaxData()
)