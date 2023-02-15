package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.utils.ImageRequestHelper


/**
 * 装备掉落率
 */
data class EquipmentIdWithOdds(
    val equipId: Int = ImageRequestHelper.UNKNOWN_EQUIP_ID,
    val odd: Int = 0
)

/**
 * 排序
 */
fun equipCompare() = Comparator<EquipmentIdWithOdds> { o1, o2 ->
    if (o1.odd > o2.odd) {
        -1
    } else if (o1.odd < o2.odd) {
        1
    } else {
        o2.equipId.compareTo(o1.equipId)
    }
}