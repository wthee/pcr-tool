package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.UniqueEquipmentMaxData
import cn.wthee.pcrtool.data.db.view.UnitPromotionBonus

/**
 * 角色相关属性
 */
data class AllAttrData(
    var sumAttr: Attr = Attr(),
    var exSkillAttr: Attr = Attr(),
    var storyAttr: Attr = Attr(),
    var equips: List<EquipmentMaxData> = arrayListOf(),
    var uniqueEquip: UniqueEquipmentMaxData = UniqueEquipmentMaxData(),
    var bonus: UnitPromotionBonus = UnitPromotionBonus()
)