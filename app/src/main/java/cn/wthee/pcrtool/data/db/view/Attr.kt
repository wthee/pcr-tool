package cn.wthee.pcrtool.data.db.view

import android.content.Context
import androidx.room.ColumnInfo
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.model.AttrCompareData
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.getString
import kotlin.random.Random

/**
 * 面板属性
 */
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
) {
    companion object {

        /**
         * 属性成长
         */
        fun setGrowthValue(rarityGrowth: UnitRarity): Attr {
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


    /**
     * 属性相加
     */
    fun add(other: Attr): Attr {
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

    /**
     * 属性相减
     */
    fun sub(other: Attr): Attr {
        this.hp -= other.hp
        this.atk -= other.atk
        this.magicStr -= other.magicStr
        this.def -= other.def
        this.magicDef -= other.magicDef
        this.physicalCritical -= other.physicalCritical
        this.magicCritical -= other.magicCritical
        this.waveHpRecovery -= other.waveHpRecovery
        this.waveEnergyRecovery -= other.waveEnergyRecovery
        this.dodge -= other.dodge
        this.physicalPenetrate -= other.physicalPenetrate
        this.magicPenetrate -= other.magicPenetrate
        this.lifeSteal -= other.lifeSteal
        this.hpRecoveryRate -= other.hpRecoveryRate
        this.energyRecoveryRate -= other.energyRecoveryRate
        this.energyReduceRate -= other.energyReduceRate
        this.accuracy -= other.accuracy
        return this
    }

    /**
     * 属性乘积
     */
    fun multiply(multi: Double): Attr {
        this.hp *= multi
        this.atk *= multi
        this.magicStr *= multi
        this.def *= multi
        this.magicDef *= multi
        this.physicalCritical *= multi
        this.magicCritical *= multi
        this.waveHpRecovery *= multi
        this.waveEnergyRecovery *= multi
        this.dodge *= multi
        this.physicalPenetrate *= multi
        this.magicPenetrate *= multi
        this.lifeSteal *= multi
        this.hpRecoveryRate *= multi
        this.energyRecoveryRate *= multi
        this.energyReduceRate *= multi
        this.accuracy *= multi
        return this
    }

    /**
     * 全部属性
     */
    fun all(context: Context = MyApplication.context): ArrayList<AttrValue> {
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
            attrs.add(AttrValue(getString(context = context, id = Constants.ATTR[i]), value))
        }
        return attrs
    }

    /**
     * 非零属性
     */
    fun allNotZero(context: Context = MyApplication.context): List<AttrValue> {
        val attrs = all(context)
        attrs.removeAll { it.value == 0.0 }
        return attrs
    }

    /**
     * 非零属性
     */
    fun summonAttr(context: Context = MyApplication.context): List<AttrValue> {
        val attrs = all(context)
        val newList = arrayListOf<AttrValue>()
        val toShowIndex = arrayListOf(0, 2, 3, 4, 5)
        toShowIndex.forEach { showIndex ->
            newList.add(attrs[showIndex])
        }
        return newList
    }

    /**
     * 属性对比差值
     */
    private fun compare(attr1: Attr): List<AttrValue> {
        val attrs = all()
        val attrs1 = attr1.all()
        val compareValue = arrayListOf<AttrValue>()
        attrs.forEachIndexed { index, attrValue ->
            compareValue.add(AttrValue(attrValue.title, attrValue.value - attrs1[index].value))
        }
        return compareValue
    }

    /**
     * 随机
     */
    fun random(): Attr {
        this.hp += Random(System.currentTimeMillis()).nextDouble()
        this.atk += Random(System.currentTimeMillis()).nextDouble()
        this.magicStr += Random(System.currentTimeMillis()).nextDouble()
        return this
    }

    /**
     * 角色属性 [Attr] ，转 [AttrCompareData] 角色 Rank 对比列表
     */
    fun compareWith(attr1: Attr): List<AttrCompareData> {
        val data = arrayListOf<AttrCompareData>()
        val list0 = this.all()
        val list1 = attr1.all()
        val list2 = attr1.compare(this)
        list0.forEachIndexed { index, attrValue ->
            data.add(
                AttrCompareData(
                    attrValue.title,
                    attrValue.value,
                    list1[index].value,
                    list2[index].value
                )
            )
        }
        return data
    }

}


