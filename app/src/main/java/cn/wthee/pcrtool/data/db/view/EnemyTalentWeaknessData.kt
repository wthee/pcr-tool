package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * 敌人弱点信息
 */
data class EnemyTalentWeaknessData(
    @PrimaryKey
    @ColumnInfo(name = "enemy_id") val enemyId: Int = 0,
    @ColumnInfo(name = "talent_1") val talent1: Int = 0,
    @ColumnInfo(name = "talent_2") val talent2: Int = 0,
    @ColumnInfo(name = "talent_3") val talent3: Int = 0,
    @ColumnInfo(name = "talent_4") val talent4: Int = 0,
    @ColumnInfo(name = "talent_5") val talent5: Int = 0,
) {
    fun getWeaknessList() = arrayListOf(talent1, talent2, talent3, talent4, talent5)
}