package cn.wthee.pcrtool.data.db.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 技能信息
 */
@Entity(tableName = "unit_skill_data")
data class CharacterSkillDataJP(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") var unit_id: Int = 0,
    @ColumnInfo(name = "union_burst") var union_burst: Int = 0,
    @ColumnInfo(name = "main_skill_1") var main_skill_1: Int = 0,
    @ColumnInfo(name = "main_skill_2") var main_skill_2: Int = 0,
    @ColumnInfo(name = "main_skill_3") var main_skill_3: Int = 0,
    @ColumnInfo(name = "main_skill_4") var main_skill_4: Int = 0,
    @ColumnInfo(name = "main_skill_5") var main_skill_5: Int = 0,
    @ColumnInfo(name = "main_skill_6") var main_skill_6: Int = 0,
    @ColumnInfo(name = "main_skill_7") var main_skill_7: Int = 0,
    @ColumnInfo(name = "main_skill_8") var main_skill_8: Int = 0,
    @ColumnInfo(name = "main_skill_9") var main_skill_9: Int = 0,
    @ColumnInfo(name = "main_skill_10") var main_skill_10: Int = 0,
    @ColumnInfo(name = "ex_skill_1") var ex_skill_1: Int = 0,
    @ColumnInfo(name = "ex_skill_evolution_1") var ex_skill_evolution_1: Int = 0,
    @ColumnInfo(name = "ex_skill_2") var ex_skill_2: Int = 0,
    @ColumnInfo(name = "ex_skill_evolution_2") var ex_skill_evolution_2: Int = 0,
    @ColumnInfo(name = "ex_skill_3") var ex_skill_3: Int = 0,
    @ColumnInfo(name = "ex_skill_evolution_3") var ex_skill_evolution_3: Int = 0,
    @ColumnInfo(name = "ex_skill_4") var ex_skill_4: Int = 0,
    @ColumnInfo(name = "ex_skill_evolution_4") var ex_skill_evolution_4: Int = 0,
    @ColumnInfo(name = "ex_skill_5") var ex_skill_5: Int = 0,
    @ColumnInfo(name = "ex_skill_evolution_5") var ex_skill_evolution_5: Int = 0,
    @ColumnInfo(name = "sp_skill_1") var sp_skill_1: Int = 0,
    @ColumnInfo(name = "sp_skill_2") var sp_skill_2: Int = 0,
    @ColumnInfo(name = "sp_skill_3") var sp_skill_3: Int = 0,
    @ColumnInfo(name = "sp_skill_4") var sp_skill_4: Int = 0,
    @ColumnInfo(name = "sp_skill_5") var sp_skill_5: Int = 0,
    @ColumnInfo(name = "union_burst_evolution") var union_burst_evolution: Int = 0,
    @ColumnInfo(name = "main_skill_evolution_1") var main_skill_evolution_1: Int = 0,
    @ColumnInfo(name = "main_skill_evolution_2") var main_skill_evolution_2: Int = 0,
    //jp
//    @Ignore
    @ColumnInfo(name = "sp_skill_evolution_1") var sp_skill_evolution_1: Int = 0,
//    @Ignore
    @ColumnInfo(name = "sp_skill_evolution_2") var sp_skill_evolution_2: Int = 0
)