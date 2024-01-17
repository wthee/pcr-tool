package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * 敌人参数
 */
data class EnemyParameterPro(
    @PrimaryKey
    @ColumnInfo(name = "enemy_id") var enemyId: Int = 0,
    @ColumnInfo(name = "unit_id") var unitId: Int = 0,
    @ColumnInfo(name = "prefab_id") var prefabId: Int = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "comment") var comment: String = "",
    @ColumnInfo(name = "normal_atk_cast_time") var atkTime: Double = 0.0,
    @ColumnInfo(name = "level") var level: Int = 0,
    @ColumnInfo(name = "rarity") var rarity: Int = 0,
    @ColumnInfo(name = "promotion_level") var promotion_level: Int = 0,
    @ColumnInfo(name = "union_burst_level") var union_burst_level: Int = 0,
    @ColumnInfo(name = "main_skill_lv_1") var main_skill_lv_1: Int = 0,
    @ColumnInfo(name = "main_skill_lv_2") var main_skill_lv_2: Int = 0,
    @ColumnInfo(name = "main_skill_lv_3") var main_skill_lv_3: Int = 0,
    @ColumnInfo(name = "main_skill_lv_4") var main_skill_lv_4: Int = 0,
    @ColumnInfo(name = "main_skill_lv_5") var main_skill_lv_5: Int = 0,
    @ColumnInfo(name = "main_skill_lv_6") var main_skill_lv_6: Int = 0,
    @ColumnInfo(name = "main_skill_lv_7") var main_skill_lv_7: Int = 0,
    @ColumnInfo(name = "main_skill_lv_8") var main_skill_lv_8: Int = 0,
    @ColumnInfo(name = "main_skill_lv_9") var main_skill_lv_9: Int = 0,
    @ColumnInfo(name = "main_skill_lv_10") var main_skill_lv_10: Int = 0,
    @ColumnInfo(name = "ex_skill_lv_1") var ex_skill_lv_1: Int = 0,
    @ColumnInfo(name = "ex_skill_lv_2") var ex_skill_lv_2: Int = 0,
    @ColumnInfo(name = "ex_skill_lv_3") var ex_skill_lv_3: Int = 0,
    @ColumnInfo(name = "ex_skill_lv_4") var ex_skill_lv_4: Int = 0,
    @ColumnInfo(name = "ex_skill_lv_5") var ex_skill_lv_5: Int = 0,
    @ColumnInfo(name = "resist_status_id") var resist_status_id: Int = 0,
    @ColumnInfo(name = "unique_equipment_flag_1") var unique_equipment_flag_1: Int = 0,
    @Embedded var attr: AttrInt = AttrInt(),
    @Ignore var partAtk: Int = 0,
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
