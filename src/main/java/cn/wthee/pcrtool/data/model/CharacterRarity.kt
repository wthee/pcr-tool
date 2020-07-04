package cn.wthee.pcrtool.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

//角色星级属性
@Entity(
    tableName = "unit_rarity",
    indices = [Index(
        value = arrayOf("unit_id"),
        unique = false,
        name = "unit_rarity_0_unit_id"
    ), Index(
        value = arrayOf("unit_material_id"),
        unique = false,
        name = "unit_rarity_0_unit_material_id"
    )],
    primaryKeys = ["unit_id", "rarity"]
)
data class CharacterRarity(
    @ColumnInfo(name = "unit_id") val unitid: Int,
    @ColumnInfo(name = "rarity") val rarity: Int,
    @ColumnInfo(name = "hp") val hp: Double,
    @ColumnInfo(name = "atk") val atk: Double,
    @ColumnInfo(name = "magic_str") val magicStr: Double,
    @ColumnInfo(name = "def") val def: Double,
    @ColumnInfo(name = "magic_def") val magicDef: Double,
    @ColumnInfo(name = "physical_critical") val physicalCritical: Double,
    @ColumnInfo(name = "magic_critical") val magicCritical: Double,
    @ColumnInfo(name = "wave_hp_recovery") val waveHpRecovery: Double,
    @ColumnInfo(name = "wave_energy_recovery") val waveEnergyRecovery: Double,
    @ColumnInfo(name = "dodge") val dodge: Double,
    @ColumnInfo(name = "physical_penetrate") val physicalPenetrate: Double,
    @ColumnInfo(name = "magic_penetrate") val magicPenetrate: Double,
    @ColumnInfo(name = "life_steal") val lifeSteal: Double,
    @ColumnInfo(name = "hp_recovery_rate") val hpRecoveryRate: Double,
    @ColumnInfo(name = "energy_recovery_rate") val energyRecoveryRate: Double,
    @ColumnInfo(name = "energy_reduce_rate") val energyReduceRate: Double,
    @ColumnInfo(name = "accuracy") val accuracy: Double,

    @ColumnInfo(name = "hp_growth") val hpGrowth: Double,
    @ColumnInfo(name = "atk_growth") val atkGrowth: Double,
    @ColumnInfo(name = "magic_str_growth") val magicStrGrowth: Double,
    @ColumnInfo(name = "def_growth") val defGrowth: Double,
    @ColumnInfo(name = "magic_def_growth") val magicDefGrowth: Double,
    @ColumnInfo(name = "physical_critical_growth") val physicalCriticalGrowth: Double,
    @ColumnInfo(name = "magic_critical_growth") val magicCriticalGrowth: Double,
    @ColumnInfo(name = "wave_hp_recovery_growth") val waveHpRecoveryGrowth: Double,
    @ColumnInfo(name = "wave_energy_recovery_growth") val waveEnergyRecoveryGrowth: Double,
    @ColumnInfo(name = "dodge_growth") val dodgeGrowth: Double,
    @ColumnInfo(name = "physical_penetrate_growth") val physicalPenetrateGrowth: Double,
    @ColumnInfo(name = "magic_penetrate_growth") val magicPenetrateGrowth: Double,
    @ColumnInfo(name = "life_steal_growth") val lifeStealGrowth: Double,
    @ColumnInfo(name = "hp_recovery_rate_growth") val hpRecoveryRateGrowth: Double,
    @ColumnInfo(name = "energy_recovery_rate_growth") val energyRecoveryRateGrowth: Double,
    @ColumnInfo(name = "energy_reduce_rate_growth") val energyReduceRateGrowth: Double,
    @ColumnInfo(name = "unit_material_id") val unitMaterialId: Int,
    @ColumnInfo(name = "consume_num") val consumeNum: Int,
    @ColumnInfo(name = "consume_gold") val consumeGold: Int,
    @ColumnInfo(name = "accuracy_growth") val accuracyGrowth: Double
)