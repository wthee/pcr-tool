package cn.wthee.pcrtool.data.view

import androidx.room.ColumnInfo
import java.io.Serializable

/**
 * 面板属性
 */
data class AttrInt(
    @ColumnInfo(name = "hp") var hp: Int,
    @ColumnInfo(name = "atk") var atk: Int,
    @ColumnInfo(name = "magic_str") var magicStr: Int,
    @ColumnInfo(name = "def") var def: Int,
    @ColumnInfo(name = "magic_def") var magicDef: Int,
    @ColumnInfo(name = "physical_critical") var physicalCritical: Int,
    @ColumnInfo(name = "magic_critical") var magicCritical: Int,
    @ColumnInfo(name = "wave_hp_recovery") var waveHpRecovery: Int,
    @ColumnInfo(name = "wave_energy_recovery") var waveEnergyRecovery: Int,
    @ColumnInfo(name = "dodge") var dodge: Int,
    @ColumnInfo(name = "physical_penetrate") var physicalPenetrate: Int,
    @ColumnInfo(name = "magic_penetrate") var magicPenetrate: Int,
    @ColumnInfo(name = "life_steal") var lifeSteal: Int,
    @ColumnInfo(name = "hp_recovery_rate") var hpRecoveryRate: Int,
    @ColumnInfo(name = "energy_recovery_rate") var energyRecoveryRate: Int,
    @ColumnInfo(name = "energy_reduce_rate") var energyReduceRate: Int,
    @ColumnInfo(name = "accuracy") var accuracy: Int
) : Serializable {

    constructor() : this(
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
    )
}