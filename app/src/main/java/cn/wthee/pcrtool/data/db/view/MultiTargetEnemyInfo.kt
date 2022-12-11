package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 * 多目标数量
 */
data class MultiTargetEnemyInfo(
    @ColumnInfo(name = "multi_enemy_id") val multiEnemyId: Int = 0,
    @ColumnInfo(name = "enemy_part_ids") val enemyPartIds: String = ""
)