package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.getString

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
    fun all(isPreview: Boolean = false): ArrayList<AttrValue> {
        return if (isPreview) {
            arrayListOf(
                AttrValue(value = 100.0),
                AttrValue(value = 1000.0),
                AttrValue(value = 10000.0)
            )
        } else {
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
                attrs.add(AttrValue(getString(Constants.ATTR[i]), value.toDouble()))
            }
            attrs
        }

    }

    /**
     * 非零属性
     */
    fun allNotZero(isPreview: Boolean = false): List<AttrValue> {
        val attrs = all(isPreview)
        attrs.removeAll { it.value == 0.0 }
        return attrs
    }

    /**
     * boss 相关属性
     */
    fun enemy(isPreview: Boolean = false): List<AttrValue> {
        val attrs = all(isPreview)
        return if (isPreview) {
            attrs
        } else {
            val newList = arrayListOf<AttrValue>()
            val toShowIndex = arrayListOf(0, 10, 2, 3, 4, 5)
            toShowIndex.forEach { showIndex ->
                newList.add(attrs[showIndex])
            }
            newList
        }
    }

    fun multiplePartEnemy(isPreview: Boolean = false): List<AttrValue> {
        val attrs = all(isPreview)
        val newList = arrayListOf<AttrValue>()
        val toShowIndex = arrayListOf(0, 10)
        toShowIndex.forEach { showIndex ->
            newList.add(attrs[showIndex])
        }
        return newList
    }
}

