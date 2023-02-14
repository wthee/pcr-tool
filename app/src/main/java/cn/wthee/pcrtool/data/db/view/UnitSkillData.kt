package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.data.enums.SkillIndexType

/**
 * 角色技能信息
 */
data class UnitSkillData(
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
    @ColumnInfo(name = "sp_union_burst") val sp_union_burst: Int,
    @ColumnInfo(name = "sp_skill_1") val sp_skill_1: Int,
    @ColumnInfo(name = "sp_skill_2") val sp_skill_2: Int,
    @ColumnInfo(name = "sp_skill_3") val sp_skill_3: Int,
    @ColumnInfo(name = "sp_skill_4") val sp_skill_4: Int,
    @ColumnInfo(name = "sp_skill_5") val sp_skill_5: Int,
    @ColumnInfo(name = "union_burst_evolution") val union_burst_evolution: Int,
    @ColumnInfo(name = "main_skill_evolution_1") val main_skill_evolution_1: Int,
    @ColumnInfo(name = "main_skill_evolution_2") val main_skill_evolution_2: Int,
    @ColumnInfo(name = "sp_skill_evolution_1") val sp_skill_evolution_1: Int,
    @ColumnInfo(name = "sp_skill_evolution_2") val sp_skill_evolution_2: Int,
) {

    fun getNormalSkillId(): ArrayList<Int> {
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
            //日服雪菲
            if ((sp_skill_1 != 1064101 || it != 1065012) && it != 0) {
                list.add(it)
            }
        }
        main_skill_2.also {
            if (it != 0) list.add(it)
        }
        main_skill_evolution_2.also {
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

    fun getSpSkillId(): ArrayList<Int> {
        val list = arrayListOf<Int>()
        //sp skill
        sp_union_burst.also {
            if (it != 0) list.add(it)
        }
        sp_skill_1.also {
            if (it != 0) list.add(it)
        }
        sp_skill_evolution_1.also {
            if (it != 0) list.add(it)
        }
        sp_skill_2.also {
            if (it != 0) list.add(it)
        }
        sp_skill_evolution_2.also {
            if (it != 0) list.add(it)
        }
        sp_skill_3.also {
            if (it != 0) list.add(it)
        }
        return list
    }

    /**
     * 获取所有技能id，不过滤为0的
     */
    fun getEnemySkillId(): ArrayList<Int> {
        return arrayListOf(
            union_burst,
            main_skill_1, main_skill_2, main_skill_3, main_skill_4, main_skill_5,
            main_skill_6, main_skill_7, main_skill_8, main_skill_9, main_skill_10,
        )
    }

    /**
     * 获取技能下标类型
     */
    fun getSkillIndexType(skillId:Int) =  when(skillId){
        union_burst -> SkillIndexType.UB
        union_burst_evolution -> SkillIndexType.UB_PLUS
        main_skill_1 -> SkillIndexType.MAIN_SKILL_1
        main_skill_2 -> SkillIndexType.MAIN_SKILL_2
        main_skill_3 -> SkillIndexType.MAIN_SKILL_3
        main_skill_4 -> SkillIndexType.MAIN_SKILL_4
        main_skill_5 -> SkillIndexType.MAIN_SKILL_5
        main_skill_6 -> SkillIndexType.MAIN_SKILL_6
        main_skill_7 -> SkillIndexType.MAIN_SKILL_7
        main_skill_8 -> SkillIndexType.MAIN_SKILL_8
        main_skill_9 -> SkillIndexType.MAIN_SKILL_9
        main_skill_10 -> SkillIndexType.MAIN_SKILL_10
        main_skill_evolution_1 -> SkillIndexType.MAIN_SKILL_1_PLUS
        main_skill_evolution_2 -> SkillIndexType.MAIN_SKILL_2_PLUS
        ex_skill_1 -> SkillIndexType.EX_1
        ex_skill_2 -> SkillIndexType.EX_2
        ex_skill_3 -> SkillIndexType.EX_3
        ex_skill_4 -> SkillIndexType.EX_4
        ex_skill_5 -> SkillIndexType.EX_5
        ex_skill_evolution_1-> SkillIndexType.EX_1_PLUS
        ex_skill_evolution_2 -> SkillIndexType.EX_2_PLUS
        ex_skill_evolution_3 -> SkillIndexType.EX_3_PLUS
        ex_skill_evolution_4 -> SkillIndexType.EX_4_PLUS
        ex_skill_evolution_5 -> SkillIndexType.EX_5_PLUS
        sp_union_burst -> SkillIndexType.SP_UB
        sp_skill_1 -> SkillIndexType.SP_SKILL_1
        sp_skill_2 -> SkillIndexType.SP_SKILL_2
        sp_skill_3 -> SkillIndexType.SP_SKILL_3
        sp_skill_4 -> SkillIndexType.SP_SKILL_4
        sp_skill_5 -> SkillIndexType.SP_SKILL_5
        sp_skill_evolution_1 -> SkillIndexType.SP_SKILL_1_PLUS
        sp_skill_evolution_2 -> SkillIndexType.SP_SKILL_2_PLUS
        else -> SkillIndexType.UNKNOWN
    }
}