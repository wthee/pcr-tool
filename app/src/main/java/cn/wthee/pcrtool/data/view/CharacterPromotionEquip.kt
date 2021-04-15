package cn.wthee.pcrtool.data.view

import androidx.room.ColumnInfo

data class CharacterPromotionEquip(
    @ColumnInfo(name = "equip_1") val equipIds1: String,
    @ColumnInfo(name = "equip_2") val equipIds2: String,
    @ColumnInfo(name = "equip_3") val equipIds3: String,
    @ColumnInfo(name = "equip_4") val equipIds4: String,
    @ColumnInfo(name = "equip_5") val equipIds5: String,
    @ColumnInfo(name = "equip_6") val equipIds6: String,
) {
    fun getAllEquipId(): MutableMap<Int, Int> {
        val ids = "$equipIds1-$equipIds2-$equipIds3-$equipIds4-$equipIds5-$equipIds6".split("-")
            .filter {
                it != "999999"
            }
        val map = mutableMapOf<Int, Int>()

        ids.forEach {
            var i = 1
            val key = it.toInt()
            if (map[key] != null) {
                i = map[key]!! + 1
            }
            map[key] = i
        }
        return map
    }
}

