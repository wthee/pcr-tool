package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.utils.ImageResourceHelper
import java.io.Serializable


/**
 * 装备合成信息
 */
data class EquipmentMaterial(
    var id: Int = ImageResourceHelper.UNKNOWN_EQUIP_ID,
    var name: String = "???",
    var count: Int = 0
) : Serializable