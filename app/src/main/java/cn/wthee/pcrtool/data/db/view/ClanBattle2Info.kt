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
    @ColumnInfo(name = "enemy_ids") var enemyIds: String = "",
    @ColumnInfo(name = "unit_ids") var unitIds: String = "",
    @Ignore
    var enemyIdList: List<Int> = arrayListOf(0, 0, 0, 0, 0),
    @Ignore
    var unitIdList: List<Int> = arrayListOf(0, 0, 0, 0, 0),
    @Ignore
    var targetCountDataList: List<ClanBattleTargetCountData> = arrayListOf()
) {

    /**
     * 获取多目标数量
     *
     * @param bossIndex boss 下标
     */
    fun getMultiCount(bossIndex: Int): Int {
        val targetCountData = targetCountDataList.firstOrNull {
            it.multiEnemyId % 10 == bossIndex + 1 + it.offset
        }
        return targetCountData?.enemyPartIds?.intArrayList?.filter { it > 0 }?.size ?: 0
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
    var offset:Int = 0
)