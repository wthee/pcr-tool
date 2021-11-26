package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.fillZero


data class ClanBattleInfo(
    @ColumnInfo(name = "clan_battle_id") val clan_battle_id: Int = -1,
    @ColumnInfo(name = "release_month") val release_month: Int = 12,
    @ColumnInfo(name = "start_time") val start_time: String = "2021-01-01",
    @ColumnInfo(name = "enemyIds") val enemyIds: String = "",
    @ColumnInfo(name = "unitIds") val unitIds: String = "",
) {

    fun getUnitIdList(selectedSection: Int): List<ClanBossTargetInfo> {
        val bossList = getAllBossIds()
        val list = arrayListOf<ClanBossTargetInfo>()
        if (bossList.isNotEmpty()) {
            val clanBossData = bossList[selectedSection]
            val unitIds = clanBossData.unitIds

            unitIds.forEachIndexed { index, s ->
                val findData = list.find {
                    it.unitId == s.toInt()
                }
                if (findData != null) {
                    findData.partEnemyIds.add(clanBossData.enemyIds[index].toInt())
                } else {
                    val data =
                        ClanBossTargetInfo(
                            selectedSection,
                            s.toInt(),
                            clanBossData.enemyIds[index].toInt(),
                            arrayListOf()
                        )
                    list.add(data)
                }
            }
        } else {
            list.add(ClanBossTargetInfo())
        }
        return if (list.size > 5) list.subList(0, 5) else list
    }


    /**
     * 获取各阶段 Boss Id 信息
     */
    fun getAllBossIds(): List<ClanBossIdData> {
        val list = arrayListOf<ClanBossIdData>()
        if (enemyIds != "") {
            val enemyList = enemyIds.split("-")
            val firstIndexs = arrayListOf<Int>()
            enemyList.forEachIndexed { index, s ->
                if (s.isNotEmpty() && s.toInt() % 100 == 1) {
                    firstIndexs.add(index)
                }
            }
            firstIndexs.add(enemyList.size)
            if (clan_battle_id == 1004) {
                list.add(
                    ClanBossIdData(
                        0,
                        listOf("302100", "302000", "300701", "304000", "302700"),
                        listOf("401010401", "401010402", "401010403", "401010404", "401011401")
                    )
                )
                list.add(
                    ClanBossIdData(
                        1,
                        listOf("302100", "302000", "300701", "304000", "302700"),
                        listOf("401011402", "401011403", "401011404", "401011405", "401011406")
                    )
                )
            } else {
                for (i in 0..firstIndexs.size - 2) {
                    list.add(
                        ClanBossIdData(
                            i,
                            unitIds.split("-").subList(firstIndexs[i], firstIndexs[i + 1]),
                            enemyIds.split("-").subList(firstIndexs[i], firstIndexs[i + 1])
                        )
                    )
                }
            }
        }

        return list
    }

    /**
     * 获取年月
     */
    fun getDate(): String {
        return start_time.substring(0, 4) + "年" + release_month.toString().fillZero() + "月"
    }
}

/**
 * 阶段 Boss 所有 id 信息
 */
data class ClanBossIdData(
    val section: Int,
    val unitIds: List<String>,
    val enemyIds: List<String>,
)

/**
 * Boss 信息、目标数信息
 */
data class ClanBossTargetInfo(
    val section: Int = 0,
    val unitId: Int = 0,
    val enemyId: Int = 0,
    var partEnemyIds: MutableList<Int> = arrayListOf()
)