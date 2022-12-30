package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.data.model.EquipmentIdWithOdds
import cn.wthee.pcrtool.data.model.equipCompare


/**
 * 主线掉落信息视图
 *
 * questType 1：普通、2：困难、3：非常困难
 */
data class QuestDetail(
    @ColumnInfo(name = "quest_id") val questId: Int = 0,
    @ColumnInfo(name = "quest_type") val questType: Int = 0,
    @ColumnInfo(name = "quest_name") val questName: String = "",
    @ColumnInfo(name = "rewards") val rewards: String = "",
    @ColumnInfo(name = "odds") val odds: String = ""
) {

    /**
     * 获取掉率
     */
    fun getOddOfEquip(equipId: String): String {
        val list1 = rewards.split('-')
        val list2 = odds.split('-')
        return list2[list1.indexOf(equipId)]
    }

    fun getAllOdd(): List<EquipmentIdWithOdds> {
        val list1 = rewards.split('-') as MutableList
        val list2 = odds.split('-') as MutableList
        val result = arrayListOf<EquipmentIdWithOdds>()
        list1.forEachIndexed { index, s ->
            if (s != "0") {
                result.add(
                    EquipmentIdWithOdds(
                        s.toInt(),
                        list2[index].toInt()
                    )
                )
            }
        }
        return result.sortedWith(equipCompare())
    }
}



