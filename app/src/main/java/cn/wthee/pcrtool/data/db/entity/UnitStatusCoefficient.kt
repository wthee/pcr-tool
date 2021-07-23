package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 战力系数
 */
@Entity(tableName = "unit_status_coefficient")
data class UnitStatusCoefficient(
    @PrimaryKey
    @ColumnInfo(name = "coefficient_id") val coefficient_id: Int,
    @ColumnInfo(name = "hp_coefficient") val hp_coefficient: Double,
    @ColumnInfo(name = "atk_coefficient") val atk_coefficient: Double,
    @ColumnInfo(name = "magic_str_coefficient") val magic_str_coefficient: Double,
    @ColumnInfo(name = "def_coefficient") val def_coefficient: Double,
    @ColumnInfo(name = "magic_def_coefficient") val magic_def_coefficient: Double,
    @ColumnInfo(name = "physical_critical_coefficient") val physical_critical_coefficient: Double,
    @ColumnInfo(name = "magic_critical_coefficient") val magic_critical_coefficient: Double,
    @ColumnInfo(name = "wave_hp_recovery_coefficient") val wave_hp_recovery_coefficient: Double,
    @ColumnInfo(name = "wave_energy_recovery_coefficient") val wave_energy_recovery_coefficient: Double,
    @ColumnInfo(name = "dodge_coefficient") val dodge_coefficient: Double,
    @ColumnInfo(name = "physical_penetrate_coefficient") val physical_penetrate_coefficient: Double,
    @ColumnInfo(name = "magic_penetrate_coefficient") val magic_penetrate_coefficient: Double,
    @ColumnInfo(name = "life_steal_coefficient") val life_steal_coefficient: Double,
    @ColumnInfo(name = "hp_recovery_rate_coefficient") val hp_recovery_rate_coefficient: Double,
    @ColumnInfo(name = "energy_recovery_rate_coefficient") val energy_recovery_rate_coefficient: Double,
    @ColumnInfo(name = "energy_reduce_rate_coefficient") val energy_reduce_rate_coefficient: Double,
    @ColumnInfo(name = "skill_lv_coefficient") val skill_lv_coefficient: Double,
    @ColumnInfo(name = "exskill_evolution_coefficient") val exskill_evolution_coefficient: Int,
    @ColumnInfo(name = "overall_coefficient") val overall_coefficient: Double,
    @ColumnInfo(name = "accuracy_coefficient") val accuracy_coefficient: Double,
    @ColumnInfo(name = "skill1_evolution_coefficient") val skill1_evolution_coefficient: Int,
    @ColumnInfo(name = "skill1_evolution_slv_coefficient") val skill1_evolution_slv_coefficient: Double,
    @ColumnInfo(name = "ub_evolution_coefficient") val ub_evolution_coefficient: Int,
    @ColumnInfo(name = "ub_evolution_slv_coefficient") val ub_evolution_slv_coefficient: Double,
)
