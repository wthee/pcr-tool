package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.model.entity.CharacterPromotionStatus
import cn.wthee.pcrtool.data.model.entity.CharacterRarity
import cn.wthee.pcrtool.data.model.entity.EquipmentData
import cn.wthee.pcrtool.data.model.entity.EquipmentEnhanceRate

//角色属性
data class CharacterAttrInfo(
    var hp: Double,
    var atk: Double,
    var magicStr: Double,
    var def: Double,
    var magicDef: Double,
    var physicalCritical: Double,
    var magicCritical: Double,
    var waveHpRecovery: Double,
    var waveEnergyRecovery: Double,
    var dodge: Double,
    var physicalPenetrate: Double,
    var magicPenetrate: Double,
    var lifeSteal: Double,
    var hpRecoveryRate: Double,
    var energyRecoveryRate: Double,
    var energyReduceRate: Double,
    var accuracy: Double
) {
    companion object {
        fun setValue(equip: EquipmentData): CharacterAttrInfo {
            return CharacterAttrInfo(
                equip.hp,
                equip.atk,
                equip.magicStr,
                equip.def,
                equip.magicDef,
                equip.physicalCritical,
                equip.magicCritical,
                equip.waveHpRecovery,
                equip.waveEnergyRecovery,
                equip.dodge,
                equip.physicalPenetrate,
                equip.magicPenetrate,
                equip.lifeSteal,
                equip.hpRecoveryRate,
                equip.energyRecoveryRate,
                equip.energyReduceRate,
                equip.accuracy

            )
        }

        fun setValue(equip: EquipmentEnhanceRate): CharacterAttrInfo {
            return CharacterAttrInfo(
                equip.hp,
                equip.atk,
                equip.magicStr,
                equip.def,
                equip.magicDef,
                equip.physicalCritical,
                equip.magicCritical,
                equip.waveHpRecovery,
                equip.waveEnergyRecovery,
                equip.dodge,
                equip.physicalPenetrate,
                equip.magicPenetrate,
                equip.lifeSteal,
                equip.hpRecoveryRate,
                equip.energyRecoveryRate,
                equip.energyReduceRate,
                equip.accuracy
            )
        }

        fun setValue(rarity: CharacterRarity): CharacterAttrInfo {
            return CharacterAttrInfo(
                rarity.hp,
                rarity.atk,
                rarity.magicStr,
                rarity.def,
                rarity.magicDef,
                rarity.physicalCritical,
                rarity.magicCritical,
                rarity.waveHpRecovery,
                rarity.waveEnergyRecovery,
                rarity.dodge,
                rarity.physicalPenetrate,
                rarity.magicPenetrate,
                rarity.lifeSteal,
                rarity.hpRecoveryRate,
                rarity.energyRecoveryRate,
                rarity.energyReduceRate,
                rarity.accuracy
            )
        }

        fun setGrowthValue(rarityGrowth: CharacterRarity): CharacterAttrInfo {
            return CharacterAttrInfo(
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

        fun setValue(status: CharacterPromotionStatus): CharacterAttrInfo {
            return CharacterAttrInfo(
                status.hp,
                status.atk,
                status.magicStr,
                status.def,
                status.magicDef,
                status.physicalCritical,
                status.magicCritical,
                status.waveHpRecovery,
                status.waveEnergyRecovery,
                status.dodge,
                status.physicalPenetrate,
                status.magicPenetrate,
                status.lifeSteal,
                status.hpRecoveryRate,
                status.energyRecoveryRate,
                status.energyReduceRate,
                status.accuracy

            )
        }
    }

}

fun CharacterAttrInfo.add(other: CharacterAttrInfo): CharacterAttrInfo {
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

fun CharacterAttrInfo.multiply(mult: Int): CharacterAttrInfo {
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
