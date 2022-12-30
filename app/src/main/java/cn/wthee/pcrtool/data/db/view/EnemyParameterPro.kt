package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.PrimaryKey

data class EnemyParameterPro(
    @PrimaryKey
    @ColumnInfo(name = "enemy_id") val enemyId: Int = 0,
    @ColumnInfo(name = "unit_id") val unitId: Int = 0,
    @ColumnInfo(name = "prefab_id") val prefabId: Int = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "comment") val comment: String = "",
    @ColumnInfo(name = "normal_atk_cast_time") val atkTime: Double = 0.0,
    @ColumnInfo(name = "level") val level: Int = 0,
    @ColumnInfo(name = "rarity") val rarity: Int = 0,
    @ColumnInfo(name = "promotion_level") val promotion_level: Int = 0,
    @ColumnInfo(name = "union_burst_level") val union_burst_level: Int = 0,
    @ColumnInfo(name = "main_skill_lv_1") val main_skill_lv_1: Int = 0,
    @ColumnInfo(name = "main_skill_lv_2") val main_skill_lv_2: Int = 0,
    @ColumnInfo(name = "main_skill_lv_3") val main_skill_lv_3: Int = 0,
    @ColumnInfo(name = "main_skill_lv_4") val main_skill_lv_4: Int = 0,
    @ColumnInfo(name = "main_skill_lv_5") val main_skill_lv_5: Int = 0,
    @ColumnInfo(name = "main_skill_lv_6") val main_skill_lv_6: Int = 0,
    @ColumnInfo(name = "main_skill_lv_7") val main_skill_lv_7: Int = 0,
    @ColumnInfo(name = "main_skill_lv_8") val main_skill_lv_8: Int = 0,
    @ColumnInfo(name = "main_skill_lv_9") val main_skill_lv_9: Int = 0,
    @ColumnInfo(name = "main_skill_lv_10") val main_skill_lv_10: Int = 0,
    @ColumnInfo(name = "ex_skill_lv_1") val ex_skill_lv_1: Int = 0,
    @ColumnInfo(name = "ex_skill_lv_2") val ex_skill_lv_2: Int = 0,
    @ColumnInfo(name = "ex_skill_lv_3") val ex_skill_lv_3: Int = 0,
    @ColumnInfo(name = "ex_skill_lv_4") val ex_skill_lv_4: Int = 0,
    @ColumnInfo(name = "ex_skill_lv_5") val ex_skill_lv_5: Int = 0,
    @ColumnInfo(name = "resist_status_id") val resist_status_id: Int = 0,
    @ColumnInfo(name = "unique_equipment_flag_1") val unique_equipment_flag_1: Int = 0,
    @Embedded var attr: AttrInt = AttrInt(),

    ) {
    fun getSkillLv(): ArrayList<Int> {
        return arrayListOf(
            union_burst_level,
            main_skill_lv_1, main_skill_lv_2, main_skill_lv_3, main_skill_lv_4, main_skill_lv_5,
            main_skill_lv_6, main_skill_lv_7, main_skill_lv_8, main_skill_lv_9, main_skill_lv_10,
        )
    }

    fun getDesc() = comment
        .replace("\\n", "")
        .replace("·", "\n\n- ")
        .replace("・", "\n\n- ")
        .replace("‧", "\n\n- ")
        .replace("　", "")

}
