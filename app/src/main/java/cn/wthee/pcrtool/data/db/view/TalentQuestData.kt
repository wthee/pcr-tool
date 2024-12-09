package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore

/**
 * 深域关卡
 */
data class TalentQuestData(
    @ColumnInfo(name = "quest_id") var questId: Int = -1,
    @ColumnInfo(name = "talent_id") var talentId: Int = 0,
    @ColumnInfo(name = "quest_name") var questName: String = "",
    @ColumnInfo(name = "enemy_id_1") var enemyId1: Int = 0,
    @ColumnInfo(name = "enemy_id_2") var enemyId2: Int = 0,
    @ColumnInfo(name = "enemy_id_3") var enemyId3: Int = 0,
    @ColumnInfo(name = "enemy_id_4") var enemyId4: Int = 0,
    @ColumnInfo(name = "enemy_id_5") var enemyId5: Int = 0,
    @ColumnInfo(name = "unit_id_1") var unitId1: Int = 0,
    @ColumnInfo(name = "unit_id_2") var unitId2: Int = 0,
    @ColumnInfo(name = "unit_id_3") var unitId3: Int = 0,
    @ColumnInfo(name = "unit_id_4") var unitId4: Int = 0,
    @ColumnInfo(name = "unit_id_5") var unitId5: Int = 0,
    @Ignore var rewardId2: Int = 0,
    @Ignore var rewardNum2: Int = 0,
    @Ignore var rewardId3: Int = 0,
    @Ignore var rewardNum3: Int = 0,
) {
    fun getEnemyIdList(): List<Int> {
        val list = arrayListOf<Int>()
        if (enemyId1 != 0) {
            list.add(enemyId1)
        }
        if (enemyId2 != 0) {
            list.add(enemyId2)
        }
        if (enemyId3 != 0) {
            list.add(enemyId3)
        }
        if (enemyId4 != 0) {
            list.add(enemyId4)
        }
        if (enemyId5 != 0) {
            list.add(enemyId5)
        }
        return list
    }

    fun getUnitIdList(): List<Int> = arrayListOf(
        formatUnitId(unitId1),
        formatUnitId(unitId2),
        formatUnitId(unitId3),
        formatUnitId(unitId4),
        formatUnitId(unitId5),
    )

    //处理角色id
    private fun formatUnitId(unitId: Int) = if (unitId > 600000) {
        (unitId - 500000 + 30) / 10 * 10 + 1
    } else {
        unitId
    }
}