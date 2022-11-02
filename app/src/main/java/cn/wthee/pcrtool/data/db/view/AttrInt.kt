package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.utils.Constants

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
     * 全部属性
     */
    fun all(): ArrayList<AttrValue> {
        val attrs = arrayListOf<AttrValue>()
        for (i in 0..16) {
            val value = when (i) {
                0 -> this.hp
                1 -> this.lifeSteal
                2 -> this.atk
                3 -> this.magicStr
                4 -> this.def
                5 -> this.magicDef
                6 -> this.physicalCritical
                7 -> this.magicCritical
                8 -> this.physicalPenetrate
                9 -> this.magicPenetrate
                10 -> this.accuracy
                11 -> this.dodge
                12 -> this.waveHpRecovery
                13 -> this.hpRecoveryRate
                14 -> this.waveEnergyRecovery
                15 -> this.energyRecoveryRate
                16 -> this.energyReduceRate
                else -> 0.0
            }
            attrs.add(AttrValue(Constants.ATTR[i], value.toDouble()))
        }
        return attrs
    }

    /**
     * 非零属性
     */
    fun allNotZero(): List<AttrValue> {
        val attrs = all()
        attrs.removeAll { it.value == 0.0 }
        return attrs
    }

    /**
     * boss 相关属性
     */
    fun enemy(): List<AttrValue> {
        val attrs = all()
        val newList = arrayListOf<AttrValue>()
        val toShowIndex = arrayListOf(0, 10, 2, 3, 4, 5)
        toShowIndex.forEach { showIndex ->
            newList.add(attrs[showIndex])
        }
        return newList
    }

    fun multiplePartEnemy(): List<AttrValue> {
        val attrs = all()
        val newList = arrayListOf<AttrValue>()
        val toShowIndex = arrayListOf(0, 10)
        toShowIndex.forEach { showIndex ->
            newList.add(attrs[showIndex])
        }
        return newList
    }
}


/**
 * 面板属性
 */
data class AttrDefaultInt(
    @ColumnInfo(name = "default_hp") var hpDefault: Int,
    @ColumnInfo(name = "default_atk") var atkDefault: Int,
    @ColumnInfo(name = "default_magic_str") var magicStrDefault: Int,
    @ColumnInfo(name = "default_def") var defDefault: Int,
    @ColumnInfo(name = "default_magic_def") var magicDefDefault: Int,
    @ColumnInfo(name = "default_physical_critical") var physicalCriticalDefault: Int,
    @ColumnInfo(name = "default_magic_critical") var magicCriticalDefault: Int,
    @ColumnInfo(name = "default_wave_hp_recovery") var waveHpRecoveryDefault: Int,
    @ColumnInfo(name = "default_wave_energy_recovery") var waveEnergyRecoveryDefault: Int,
    @ColumnInfo(name = "default_dodge") var dodgeDefault: Int,
    @ColumnInfo(name = "default_physical_penetrate") var physicalPenetrateDefault: Int,
    @ColumnInfo(name = "default_magic_penetrate") var magicPenetrateDefault: Int,
    @ColumnInfo(name = "default_life_steal") var lifeStealDefault: Int,
    @ColumnInfo(name = "default_hp_recovery_rate") var hpRecoveryRateDefault: Int,
    @ColumnInfo(name = "default_energy_recovery_rate") var energyRecoveryRateDefault: Int,
    @ColumnInfo(name = "default_energy_reduce_rate") var energyReduceRateDefault: Int,
    @ColumnInfo(name = "default_accuracy") var accuracyDefault: Int
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
     * 全部属性
     */
    fun all(): ArrayList<AttrValue> {
        val attrs = arrayListOf<AttrValue>()
        for (i in 0..16) {
            val value = when (i) {
                0 -> this.hpDefault
                1 -> this.lifeStealDefault
                2 -> this.atkDefault
                3 -> this.magicStrDefault
                4 -> this.defDefault
                5 -> this.magicDefDefault
                6 -> this.physicalCriticalDefault
                7 -> this.magicCriticalDefault
                8 -> this.physicalPenetrateDefault
                9 -> this.magicPenetrateDefault
                10 -> this.accuracyDefault
                11 -> this.dodgeDefault
                12 -> this.waveHpRecoveryDefault
                13 -> this.hpRecoveryRateDefault
                14 -> this.waveEnergyRecoveryDefault
                15 -> this.energyRecoveryRateDefault
                16 -> this.energyReduceRateDefault
                else -> 0.0
            }
            attrs.add(AttrValue(Constants.ATTR[i], value.toDouble()))
        }
        return attrs
    }

    /**
     * 非零属性
     */
    fun allNotZero(): List<AttrValue> {
        val attrs = all()
        attrs.removeAll { it.value == 0.0 }
        return attrs
    }
}