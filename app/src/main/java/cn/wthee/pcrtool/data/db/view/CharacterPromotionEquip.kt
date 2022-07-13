package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

data class CharacterPromotionEquip(
    @ColumnInfo(name = "equip_1") var equipIds1: String,
    @ColumnInfo(name = "equip_2") var equipIds2: String,
    @ColumnInfo(name = "equip_3") var equipIds3: String,
    @ColumnInfo(name = "equip_4") var equipIds4: String,
    @ColumnInfo(name = "equip_5") var equipIds5: String,
    @ColumnInfo(name = "equip_6") var equipIds6: String,
) {
    fun getAllEquipId(): MutableMap<Int, Int> {
        val ids = "$equipIds1-$equipIds2-$equipIds3-$equipIds4-$equipIds5-$equipIds6".split("-")
            .filter {
                it != "999999"
            }
        val map = mutableMapOf<Int, Int>()

        ids.forEach {
            if (it != "") {
                var i = 1
                val key = it.toInt()
                if (map[key] != null) {
                    i = map[key]!! + 1
                }
                map[key] = i
            }
        }
        return map
    }
}