package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 公会战信息
 */
data class ClanBattleInfo(
    @ColumnInfo(name = "clan_battle_id") var clanBattleId: Int = -1,
    @ColumnInfo(name = "release_month") var releaseMonth: Int = 12,
    @ColumnInfo(name = "start_time") var startTime: String = "2021-01-01",
    @ColumnInfo(name = "min_phase") var minPhase: Int = 1,
    @ColumnInfo(name = "max_phase") var maxPhase: Int = 1,
    @ColumnInfo(name = "enemy_ids") var enemyIds: String = "0-0-0-0-0",
    @ColumnInfo(name = "unit_ids") var unitIds: String = "0-0-0-0-0",
    @Ignore
    var bossList: List<ClanBattleBossData> = arrayListOf()
) {

    /**
     * 获取多目标数量
     *
     * @param bossIndex boss 下标
     */
    fun getMultiCount(bossIndex: Int) = if (bossList.size == 5) {
        bossList[bossIndex].targetCountData.enemyPartIds.intArrayList.filter { it > 0 }.size
    } else {
        0
    }

    /**
     * 获取弱点属性
     *
     * @param bossIndex boss 下标
     */
    fun getWeakness(bossIndex: Int) = if (bossList.size == 5) {
        bossList[bossIndex].weaknessData
    } else {
        null
    }
}

/**
 * 多目标数量
 */
data class ClanBattleTargetCountData(
    @ColumnInfo(name = "clan_battle_id") var clanBattleId: Int = -1,
    @ColumnInfo(name = "multi_enemy_id") var multiEnemyId: Int = 0,
    @ColumnInfo(name = "enemy_part_ids") var enemyPartIds: String = "",
    @Ignore
    var offset: Int = 0
)

data class ClanBattleBossData(
    var enemyId: Int = 0,
    var unitId: Int = 0,
    var targetCountData: ClanBattleTargetCountData = ClanBattleTargetCountData(),
    var weaknessData: EnemyTalentWeaknessData? = null,
)