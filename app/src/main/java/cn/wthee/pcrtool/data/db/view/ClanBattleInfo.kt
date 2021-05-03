package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.fillZero
import java.io.Serializable

data class ClanBattleInfo(
    @ColumnInfo(name = "clan_battle_id") val clan_battle_id: Int,
    @ColumnInfo(name = "release_month") val release_month: Int,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "enemyIds") val enemyIds: String,
    @ColumnInfo(name = "unitIds") val unitIds: String,
) : Serializable {

    fun getUnitIdList(selectedSection: Int): MutableMap<String, Int> {
        val ids = getSection()[selectedSection - 1]
        val map = mutableMapOf<String, Int>()
        ids?.forEachIndexed { index, s ->
            if (map[s] != null) {
                //计数 + 1
                map[s] = map[s]!! + 1
            } else {
                map[s] = 1
            }
        }
        return map
    }

//    fun getEnemyList(selectedSection: Int): ArrayList<Int> {
//        val list = enemyIds.split("-")
//        val intList = arrayListOf<Int>()
//        list.forEachIndexed { index, s ->
//            if (index % section == selectedSection - 1) {
//                intList.add(s.toInt())
//            }
//        }
//        return intList
//    }

    /**
     * 获取阶段数
     */
    fun getSection(): MutableMap<Int, List<String>> {
        //305700-302000-300602-304101-304101-304101-300100
        val enemyList = enemyIds.split("-")
        val eachSectionUnit = mutableMapOf<Int, List<String>>()
        val firstIndexs = arrayListOf<Int>()
        enemyList.forEachIndexed { index, s ->
            if (s.toInt() % 100 == 1) {
                firstIndexs.add(index)
            }
        }
        firstIndexs.add(enemyList.size)
        for (i in 0..firstIndexs.size - 2) {
            eachSectionUnit[i] = unitIds.split("-").subList(firstIndexs[i], firstIndexs[i + 1])
        }
        return eachSectionUnit
    }

    /**
     * 获取年月
     */
    fun getDate(): String {
        return start_time.substring(0, 4) + "年" + release_month.toString().fillZero() + "月"
    }
}