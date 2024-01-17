package cn.wthee.pcrtool.data.db.view

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.getString

/**
 * 战力系数
 */
data class UnitStatusCoefficient(
    @PrimaryKey
    @ColumnInfo(name = "coefficient_id") val coefficient_id: Int = 1,
    @ColumnInfo(name = "hp_coefficient") val hp_coefficient: Double = 0.0,
    @ColumnInfo(name = "atk_coefficient") val atk_coefficient: Double = 0.0,
    @ColumnInfo(name = "magic_str_coefficient") val magic_str_coefficient: Double = 0.0,
    @ColumnInfo(name = "def_coefficient") val def_coefficient: Double = 0.0,
    @ColumnInfo(name = "magic_def_coefficient") val magic_def_coefficient: Double = 0.0,
    @ColumnInfo(name = "physical_critical_coefficient") val physical_critical_coefficient: Double = 0.0,
    @ColumnInfo(name = "magic_critical_coefficient") val magic_critical_coefficient: Double = 0.0,
    @ColumnInfo(name = "wave_hp_recovery_coefficient") val wave_hp_recovery_coefficient: Double = 0.0,
    @ColumnInfo(name = "wave_energy_recovery_coefficient") val wave_energy_recovery_coefficient: Double = 0.0,
    @ColumnInfo(name = "dodge_coefficient") val dodge_coefficient: Double = 0.0,
    @ColumnInfo(name = "physical_penetrate_coefficient") val physical_penetrate_coefficient: Double = 0.0,
    @ColumnInfo(name = "magic_penetrate_coefficient") val magic_penetrate_coefficient: Double = 0.0,
    @ColumnInfo(name = "life_steal_coefficient") val life_steal_coefficient: Double = 0.0,
    @ColumnInfo(name = "hp_recovery_rate_coefficient") val hp_recovery_rate_coefficient: Double = 0.0,
    @ColumnInfo(name = "energy_recovery_rate_coefficient") val energy_recovery_rate_coefficient: Double = 0.0,
    @ColumnInfo(name = "energy_reduce_rate_coefficient") val energy_reduce_rate_coefficient: Double = 0.0,
    @ColumnInfo(name = "skill_lv_coefficient") val skill_lv_coefficient: Double = 0.0,
    @ColumnInfo(name = "exskill_evolution_coefficient") val exskill_evolution_coefficient: Int = 0,
    @ColumnInfo(name = "overall_coefficient") val overall_coefficient: Double = 0.0,
    @ColumnInfo(name = "accuracy_coefficient") val accuracy_coefficient: Double = 0.0,
    @ColumnInfo(name = "skill1_evolution_coefficient") val skill1_evolution_coefficient: Int = 0,
    @ColumnInfo(name = "skill1_evolution_slv_coefficient") val skill1_evolution_slv_coefficient: Double = 0.0,
    @ColumnInfo(name = "ub_evolution_coefficient") val ub_evolution_coefficient: Int = 0,
    @ColumnInfo(name = "ub_evolution_slv_coefficient") val ub_evolution_slv_coefficient: Double = 0.0,
) {
    fun getAttrValueList(context: Context): ArrayList<AttrValue> {
        val coeList = arrayListOf<AttrValue>()
        for (i in 0..16) {
            val value = when (i) {
                0 -> this.hp_coefficient
                1 -> this.life_steal_coefficient
                2 -> this.atk_coefficient
                3 -> this.magic_str_coefficient
                4 -> this.def_coefficient
                5 -> this.magic_def_coefficient
                6 -> this.physical_critical_coefficient
                7 -> this.magic_critical_coefficient
                8 -> this.physical_penetrate_coefficient
                9 -> this.magic_penetrate_coefficient
                10 -> this.accuracy_coefficient
                11 -> this.dodge_coefficient
                12 -> this.wave_hp_recovery_coefficient
                13 -> this.hp_recovery_rate_coefficient
                14 -> this.wave_energy_recovery_coefficient
                15 -> this.energy_recovery_rate_coefficient
                16 -> this.energy_reduce_rate_coefficient
                else -> 0.0
            }
            coeList.add(AttrValue(getString(id = Constants.ATTR[i], context), value))
        }
        return coeList
    }
}
