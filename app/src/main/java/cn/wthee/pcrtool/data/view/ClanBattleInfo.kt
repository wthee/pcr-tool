package cn.wthee.pcrtool.data.view

import androidx.room.ColumnInfo

data class ClanBattleInfo(
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "release_month") val release_month: Int,
    @ColumnInfo(name = "clan_battle_id") val clan_battle_id: Int,
    @ColumnInfo(name = "section") val section: Int,
    @ColumnInfo(name = "enemyIds") val enemyIds: String,
    @ColumnInfo(name = "unitIds") val unitIds: String,
) {
    fun getUnitIdList(): ArrayList<Int> {
        val list = unitIds.split("-")
        val intList = arrayListOf<Int>()
        list.forEachIndexed { index, s ->
            if (index % section == 0) {
                intList.add(s.toInt())
            }
        }
        return intList
    }
}