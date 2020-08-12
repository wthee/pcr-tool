package cn.wthee.pcrtool.data.model

import androidx.room.ColumnInfo


//最终信息
data class EquipmentDropInfo(
    @ColumnInfo(name = "eid") val eid: Int,
    @ColumnInfo(name = "quest_id") val questId: Int,
    @ColumnInfo(name = "quest_name") val questName: String,
    @ColumnInfo(name = "rewards") val rewards: String,
    @ColumnInfo(name = "odds") val odds: String
) {
    fun getNum() = questName.split(" ")[1]

    fun getName() = questName.split(" ")[0]

    fun getAllOdd(): ArrayList<EquipmentIdWithOdd> {
        val list1 = rewards.split('-') as MutableList
        val list2 = odds.split('-') as MutableList
        val result = arrayListOf<EquipmentIdWithOdd>()
        list1.forEachIndexed { index, s ->
            if (s != "0") {
                result.add(EquipmentIdWithOdd(s.toInt(), list2[index].toInt()))
            }
        }
        result.sortByDescending { it.odd }
        return result
    }
}


data class EquipmentIdWithOdd(
    val eid: Int,
    val odd: Int
)

//合成信息
data class EquipmentMaterial(
    var id: Int,
    var name: String,
    var count: Int
)