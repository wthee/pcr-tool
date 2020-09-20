package cn.wthee.pcrtool.data.model.entity

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.data.model.AttrData
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.UNKNOW_EQUIP_ID
import java.io.Serializable

data class EquipmentMaxData(
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "equipment_name") val equipmentName: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "craft_flg") val craftFlg: Int,
    @ColumnInfo(name = "require_level") val requireLevel: Int,
    @ColumnInfo(name = "promotion_level") val promotionLevel: Int,
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
    @ColumnInfo(name = "accuracy") val accuracy: Double
) : Serializable {

    fun getDesc() = description.replace("\\n", "")

    companion object {
        fun unknow() =
            EquipmentMaxData(
                UNKNOW_EQUIP_ID,
                "？？？",
                "",
                "",
                0,
                0,
                0,
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

}

fun EquipmentMaxData.getList(): List<AttrData> {
    val attrs = arrayListOf<AttrData>()
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
        if (value > 0) {
            attrs.add(AttrData(Constants.ATTR[i], value))
        }
    }
    return attrs
}



