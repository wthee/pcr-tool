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

    fun getUnitIdList(selectedSection: Int): List<ClanBossTargetInfo> {
        val clanBossData = getAllBossInfo()[selectedSection - 1]
        val unitIds = clanBossData.unitIds
        val list = arrayListOf<ClanBossTargetInfo>()

        unitIds.forEachIndexed { index, s ->
            val findData = list.find {
                it.unitId == s.toInt()
            }
            if (findData != null) {
                findData.targetCount = findData.targetCount + 1
            } else {
                val data =
                    ClanBossTargetInfo(
                        selectedSection,
                        s.toInt(),
                        clanBossData.enemyIds[index].toInt(),
                        1
                    )
                list.add(data)
            }
        }
        return list
    }


    /**
     * 获取各阶段 Boss Id 信息
     */
    fun getAllBossInfo(): List<ClanBossData> {
        val enemyList = enemyIds.split("-")
        val firstIndexs = arrayListOf<Int>()
        enemyList.forEachIndexed { index, s ->
            if (s.toInt() % 100 == 1) {
                firstIndexs.add(index)
            }
        }
        firstIndexs.add(enemyList.size)
        val list = arrayListOf<ClanBossData>()
        if (clan_battle_id == 1004) {
            list.add(
                ClanBossData(
                    0,
                    listOf("302100", "302000", "300701", "304000", "302700"),
                    listOf("401010401", "401010402", "401010403", "401010404", "401011401")
                )
            )
            list.add(

                ClanBossData(
                    1,
                    listOf("302100", "302000", "300701", "304000", "302700"),
                    listOf("401011402", "401011403", "401011404", "401011405", "401011406")
                )
            )
        } else {
            for (i in 0..firstIndexs.size - 2) {
                list.add(
                    ClanBossData(
                        i,
                        unitIds.split("-").subList(firstIndexs[i], firstIndexs[i + 1]),
                        enemyIds.split("-").subList(firstIndexs[i], firstIndexs[i + 1])
                    )
                )
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
data class ClanBossData(
    val section: Int,
    val unitIds: List<String>,
    val enemyIds: List<String>,
)

/**
 * Boss 信息、目标数信息
 */
data class ClanBossTargetInfo(
    val section: Int,
    val unitId: Int,
    val enemyId: Int,
    var targetCount: Int = 1
)