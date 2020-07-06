package cn.wthee.pcrtool.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity

//角色属性状态
@Entity(
    tableName = "enemy_parameter",
    primaryKeys = ["enemy_id"]
)
data class EnemyData(
    @ColumnInfo(name = "enemy_id") val enemy_id: Int,
    @ColumnInfo(name = "unit_id") val unit_id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "level") val level: Int,
    @ColumnInfo(name = "rarity") val rarity: Int,
    @ColumnInfo(name = "promotion_level") val promotion_level: Int,
    @ColumnInfo(name = "hp") val hp: Int,
    @ColumnInfo(name = "atk") val atk: Int,
    @ColumnInfo(name = "magic_str") val magic_str: Int,
    @ColumnInfo(name = "def") val def: Int,
    @ColumnInfo(name = "magic_def") val magic_def: Int,
    @ColumnInfo(name = "physical_critical") val physical_critical: Int,
    @ColumnInfo(name = "magic_critical") val magic_critical: Int,
    @ColumnInfo(name = "wave_hp_recovery") val wave_hp_recovery: Int,
    @ColumnInfo(name = "wave_energy_recovery") val wave_energy_recovery: Int,
    @ColumnInfo(name = "dodge") val dodge: Int,
    @ColumnInfo(name = "physical_penetrate") val physical_penetrate: Int,
    @ColumnInfo(name = "magic_penetrate") val magic_penetrate: Int,
    @ColumnInfo(name = "life_steal") val life_steal: Int,
    @ColumnInfo(name = "hp_recovery_rate") val hp_recovery_rate: Int,
    @ColumnInfo(name = "energy_recovery_rate") val energy_recovery_rate: Int,
    @ColumnInfo(name = "energy_reduce_rate") val energy_reduce_rate: Int,
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
    @ColumnInfo(name = "accuracy") val accuracy: Int
)