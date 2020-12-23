package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.data.bean.AttrValue
import cn.wthee.pcrtool.data.db.entity.CharacterRarity
import cn.wthee.pcrtool.utils.Constants
import java.io.Serializable

//角色属性
data class Attr(
    @ColumnInfo(name = "hp") var hp: Double,
    @ColumnInfo(name = "atk") var atk: Double,
    @ColumnInfo(name = "magic_str") var magicStr: Double,
    @ColumnInfo(name = "def") var def: Double,
    @ColumnInfo(name = "magic_def") var magicDef: Double,
    @ColumnInfo(name = "physical_critical") var physicalCritical: Double,
    @ColumnInfo(name = "magic_critical") var magicCritical: Double,
    @ColumnInfo(name = "wave_hp_recovery") var waveHpRecovery: Double,
    @ColumnInfo(name = "wave_energy_recovery") var waveEnergyRecovery: Double,
    @ColumnInfo(name = "dodge") var dodge: Double,
    @ColumnInfo(name = "physical_penetrate") var physicalPenetrate: Double,
    @ColumnInfo(name = "magic_penetrate") var magicPenetrate: Double,
    @ColumnInfo(name = "life_steal") var lifeSteal: Double,
    @ColumnInfo(name = "hp_recovery_rate") var hpRecoveryRate: Double,
    @ColumnInfo(name = "energy_recovery_rate") var energyRecoveryRate: Double,
    @ColumnInfo(name = "energy_reduce_rate") var energyReduceRate: Double,
    @ColumnInfo(name = "accuracy") var accuracy: Double
) : Serializable {
    companion object {

        fun setGrowthValue(rarityGrowth: CharacterRarity): Attr {
            return Attr(
                rarityGrowth.hpGrowth,
                rarityGrowth.atkGrowth,
                rarityGrowth.magicStrGrowth,
                rarityGrowth.defGrowth,
                rarityGrowth.magicDefGrowth,
                rarityGrowth.physicalCriticalGrowth,
                rarityGrowth.magicCriticalGrowth,
                rarityGrowth.waveHpRecoveryGrowth,
                rarityGrowth.waveEnergyRecoveryGrowth,
                rarityGrowth.dodgeGrowth,
                rarityGrowth.physicalPenetrateGrowth,
                rarityGrowth.magicPenetrateGrowth,
                rarityGrowth.lifeStealGrowth,
                rarityGrowth.hpRecoveryRateGrowth,
                rarityGrowth.energyRecoveryRateGrowth,
                rarityGrowth.energyReduceRateGrowth,
                rarityGrowth.accuracyGrowth
            )
        }

    }

    constructor() : this(
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
    )
}


fun Attr.add(other: Attr): Attr {
    this.hp += other.hp
    this.atk += other.atk
    this.magicStr += other.magicStr
    this.def += other.def
    this.magicDef += other.magicDef
    this.physicalCritical += other.physicalCritical
    this.magicCritical += other.magicCritical
    this.waveHpRecovery += other.waveHpRecovery
    this.waveEnergyRecovery += other.waveEnergyRecovery
    this.dodge += other.dodge
    this.physicalPenetrate += other.physicalPenetrate
    this.magicPenetrate += other.magicPenetrate
    this.lifeSteal += other.lifeSteal
    this.hpRecoveryRate += other.hpRecoveryRate
    this.energyRecoveryRate += other.energyRecoveryRate
    this.energyReduceRate += other.energyReduceRate
    this.accuracy += other.accuracy
    return this
}

fun Attr.multiply(mult: Int): Attr {
    this.hp *= mult
    this.atk *= mult
    this.magicStr *= mult
    this.def *= mult
    this.magicDef *= mult
    this.physicalCritical *= mult
    this.magicCritical *= mult
    this.waveHpRecovery *= mult
    this.waveEnergyRecovery *= mult
    this.dodge *= mult
    this.physicalPenetrate *= mult
    this.magicPenetrate *= mult
    this.lifeSteal *= mult
    this.hpRecoveryRate *= mult
    this.energyRecoveryRate *= mult
    this.energyReduceRate *= mult
    this.accuracy *= mult
    return this
}

//全部属性
fun Attr.all(): ArrayList
<AttrValue> {
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
        attrs.add(AttrValue(Constants.ATTR[i], value))
    }
    return attrs
}

//非零属性
fun Attr.allNotZero(): List<AttrValue> {
    val attrs = all()
    attrs.removeAll { it.value == 0.0 }
    return attrs
}

//非零属性
fun Attr.compare(attr1: Attr): List<AttrValue> {
    val attrs = all()
    val attrs1 = attr1.all()
    val compareValue = arrayListOf<AttrValue>()
    attrs.forEachIndexed { index, attrValue ->
        compareValue.add(AttrValue(attrValue.title, attrValue.value - attrs1[index].value))
    }
    return compareValue
}
