package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import cn.wthee.pcrtool.utils.fillZero
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 团队战信息
 */
data class ClanBattleInfo(
    @ColumnInfo(name = "clan_battle_id") var clanBattleId: Int = -1,
    @ColumnInfo(name = "release_month") var releaseMonth: Int = 12,
    @ColumnInfo(name = "start_time") var startTime: String = "2021-01-01",
    @ColumnInfo(name = "max_phase") var phase: Int = 1,
    @ColumnInfo(name = "enemy_ids") var enemyIds: String = "1-1-1-1-1",
    @ColumnInfo(name = "unit_ids") var unitIds: String = "1-1-1-1-1",
    @Ignore
    var enemyIdList: List<Int> = arrayListOf(0, 0, 0, 0, 0),
    @Ignore
    var unitIdList: List<Int> = arrayListOf(0, 0, 0, 0, 0),
    @Ignore
    var targetCountData: ClanBattleTargetCountData = ClanBattleTargetCountData()
) {

    /**
     * 获取多目标数量
     *
     * @param bossIndex boss 下标
     */
    fun getMultiCount(bossIndex: Int): Int {
        return if (bossIndex + 1 == targetCountData.multiEnemyId % 10) {
            targetCountData.enemyPartIds.intArrayList.filter { it > 0 }.size
        } else {
            0
        }
    }

    /**
     * 获取年月
     */
    fun getDate(): String {
        return startTime.substring(0, 4) + "年" + releaseMonth.toString().fillZero() + "月"
    }
}

/**
 * 多目标数量
 */
data class ClanBattleTargetCountData(
    @ColumnInfo(name = "clan_battle_id") val clanBattleId: Int = -1,
    @ColumnInfo(name = "multi_enemy_id") val multiEnemyId: Int = 0,
    @ColumnInfo(name = "enemy_part_ids") val enemyPartIds: String = ""
)