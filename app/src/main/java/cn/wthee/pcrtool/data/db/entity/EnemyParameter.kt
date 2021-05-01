package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.data.db.view.AttrInt

@Entity(tableName = "enemy_parameter")
data class EnemyParameter(
    @PrimaryKey
    @ColumnInfo(name = "enemy_id") val enemy_id: Int,
    @ColumnInfo(name = "unit_id") val unit_id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "level") val level: Int,
    @ColumnInfo(name = "rarity") val rarity: Int,
    @ColumnInfo(name = "promotion_level") val promotion_level: Int,
    @ColumnInfo(name = "union_burst_level") val union_burst_level: Int,
    @ColumnInfo(name = "main_skill_lv_1") val main_skill_lv_1: Int,
    @ColumnInfo(name = "main_skill_lv_2") val main_skill_lv_2: Int,
    @ColumnInfo(name = "main_skill_lv_3") val main_skill_lv_3: Int,
    @ColumnInfo(name = "main_skill_lv_4") val main_skill_lv_4: Int,
    @ColumnInfo(name = "main_skill_lv_5") val main_skill_lv_5: Int,
    @ColumnInfo(name = "main_skill_lv_6") val main_skill_lv_6: Int,
    @ColumnInfo(name = "main_skill_lv_7") val main_skill_lv_7: Int,
    @ColumnInfo(name = "main_skill_lv_8") val main_skill_lv_8: Int,
    @ColumnInfo(name = "main_skill_lv_9") val main_skill_lv_9: Int,
    @ColumnInfo(name = "main_skill_lv_10") val main_skill_lv_10: Int,
    @ColumnInfo(name = "ex_skill_lv_1") val ex_skill_lv_1: Int,
    @ColumnInfo(name = "ex_skill_lv_2") val ex_skill_lv_2: Int,
    @ColumnInfo(name = "ex_skill_lv_3") val ex_skill_lv_3: Int,
    @ColumnInfo(name = "ex_skill_lv_4") val ex_skill_lv_4: Int,
    @ColumnInfo(name = "ex_skill_lv_5") val ex_skill_lv_5: Int,
    @ColumnInfo(name = "resist_status_id") val resist_status_id: Int,
    @ColumnInfo(name = "unique_equipment_flag_1") val unique_equipment_flag_1: Int,
//    @ColumnInfo(name = "break_durability") val break_durability: Int,
//    @ColumnInfo(name = "virtual_hp") val virtual_hp: Int,
    @Embedded var attr: AttrInt = AttrInt(),
) {
    fun getSkillLv(): ArrayList<Int> {
        return arrayListOf(
            union_burst_level,
            main_skill_lv_1, main_skill_lv_2, main_skill_lv_3, main_skill_lv_4, main_skill_lv_5,
            main_skill_lv_6, main_skill_lv_7, main_skill_lv_8, main_skill_lv_9, main_skill_lv_10,
        )
    }
}
