package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 * ex装备默认属性
 */
data class AttrDefaultInt(
    @ColumnInfo(name = "default_hp") var hp: Int,
    @ColumnInfo(name = "default_atk") var atk: Int,
    @ColumnInfo(name = "default_magic_str") var magicStr: Int,
    @ColumnInfo(name = "default_def") var def: Int,
    @ColumnInfo(name = "default_magic_def") var magicDef: Int,
    @ColumnInfo(name = "default_physical_critical") var physicalCritical: Int,
    @ColumnInfo(name = "default_magic_critical") var magicCritical: Int,
    @ColumnInfo(name = "default_wave_hp_recovery") var waveHpRecovery: Int,
    @ColumnInfo(name = "default_wave_energy_recovery") var waveEnergyRecovery: Int,
    @ColumnInfo(name = "default_dodge") var dodge: Int,
    @ColumnInfo(name = "default_physical_penetrate") var physicalPenetrate: Int,
    @ColumnInfo(name = "default_magic_penetrate") var magicPenetrate: Int,
    @ColumnInfo(name = "default_life_steal") var lifeSteal: Int,
    @ColumnInfo(name = "default_hp_recovery_rate") var hpRecoveryRate: Int,
    @ColumnInfo(name = "default_energy_recovery_rate") var energyRecoveryRate: Int,
    @ColumnInfo(name = "default_energy_reduce_rate") var energyReduceRate: Int,
    @ColumnInfo(name = "default_accuracy") var accuracy: Int
) {
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

    /**
     * 转化为 AttrInt
     */
    fun toAttrInt() = AttrInt(
        hp = this.hp,
        lifeSteal = this.lifeSteal,
        atk = this.atk,
        magicStr = this.magicStr,
        def = this.def,
        magicDef = this.magicDef,
        physicalCritical = this.physicalCritical,
        magicCritical = this.magicCritical,
        physicalPenetrate = this.physicalPenetrate,
        magicPenetrate = this.magicPenetrate,
        accuracy = this.accuracy,
        dodge = this.dodge,
        waveHpRecovery = this.waveHpRecovery,
        hpRecoveryRate = this.hpRecoveryRate,
        waveEnergyRecovery = this.waveEnergyRecovery,
        energyRecoveryRate = this.energyRecoveryRate,
        energyReduceRate = this.energyReduceRate,
    )
}