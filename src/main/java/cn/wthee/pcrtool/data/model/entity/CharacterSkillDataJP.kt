package cn.wthee.pcrtool.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unit_skill_data")
data class CharacterSkillDataJP(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") val unit_id: Int,
    @ColumnInfo(name = "union_burst") val union_burst: Int,
    @ColumnInfo(name = "main_skill_1") val main_skill_1: Int,
    @ColumnInfo(name = "main_skill_2") val main_skill_2: Int,
    @ColumnInfo(name = "main_skill_3") val main_skill_3: Int,
    @ColumnInfo(name = "main_skill_4") val main_skill_4: Int,
    @ColumnInfo(name = "main_skill_5") val main_skill_5: Int,
    @ColumnInfo(name = "main_skill_6") val main_skill_6: Int,
    @ColumnInfo(name = "main_skill_7") val main_skill_7: Int,
    @ColumnInfo(name = "main_skill_8") val main_skill_8: Int,
    @ColumnInfo(name = "main_skill_9") val main_skill_9: Int,
    @ColumnInfo(name = "main_skill_10") val main_skill_10: Int,
    @ColumnInfo(name = "ex_skill_1") val ex_skill_1: Int,
    @ColumnInfo(name = "ex_skill_evolution_1") val ex_skill_evolution_1: Int,
    @ColumnInfo(name = "ex_skill_2") val ex_skill_2: Int,
    @ColumnInfo(name = "ex_skill_evolution_2") val ex_skill_evolution_2: Int,
    @ColumnInfo(name = "ex_skill_3") val ex_skill_3: Int,
    @ColumnInfo(name = "ex_skill_evolution_3") val ex_skill_evolution_3: Int,
    @ColumnInfo(name = "ex_skill_4") val ex_skill_4: Int,
    @ColumnInfo(name = "ex_skill_evolution_4") val ex_skill_evolution_4: Int,
    @ColumnInfo(name = "ex_skill_5") val ex_skill_5: Int,
    @ColumnInfo(name = "ex_skill_evolution_5") val ex_skill_evolution_5: Int,
    @ColumnInfo(name = "sp_skill_1") val sp_skill_1: Int,
    @ColumnInfo(name = "sp_skill_2") val sp_skill_2: Int,
    @ColumnInfo(name = "sp_skill_3") val sp_skill_3: Int,
    @ColumnInfo(name = "sp_skill_4") val sp_skill_4: Int,
    @ColumnInfo(name = "sp_skill_5") val sp_skill_5: Int,
    @ColumnInfo(name = "union_burst_evolution") val union_burst_evolution: Int,
    @ColumnInfo(name = "main_skill_evolution_1") val main_skill_evolution_1: Int,
    @ColumnInfo(name = "main_skill_evolution_2") val main_skill_evolution_2: Int,
    //jp
    @ColumnInfo(name = "sp_skill_evolution_1") val sp_skill_evolution_1: Int,
    @ColumnInfo(name = "sp_skill_evolution_2") val sp_skill_evolution_2: Int
) {
    fun getAllSkillId(): ArrayList<Int> {
        val list = arrayListOf<Int>()
        union_burst.also {
            if (it != 0) list.add(it)
        }
        union_burst_evolution.also {
            if (it != 0) list.add(it)
        }
        main_skill_1.also {
            if (it != 0) list.add(it)
        }
        main_skill_evolution_1.also {
            if (it != 0) list.add(it)
        }
        main_skill_2.also {
            if (it != 0) list.add(it)
        }
        ex_skill_1.also {
            if (it != 0) list.add(it)
        }
        ex_skill_evolution_1.also {
            if (it != 0) list.add(it)
        }

        return list
    }
}