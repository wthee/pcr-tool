package cn.wthee.pcrtool.data.model.entity

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.AttrData
import cn.wthee.pcrtool.utils.Constants.UNKNOW_EQUIP_ID
import java.io.Serializable

data class UniqueEquipmentMaxData(
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "equipment_name") val equipmentName: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "max_level") val maxLevel: Int,
    @ColumnInfo(name = "rank") val rank: Int,
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
            UniqueEquipmentMaxData(
                0,
                UNKNOW_EQUIP_ID,
                "？？？",
                "",
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

    fun getAttrs(): ArrayList<AttrData> {
        val attrs = arrayListOf<AttrData>()
        val res = MyApplication.getContext().resources

        if (hp != 0.0) attrs.add(
            AttrData(
                "HP",
                hp
            )
        )
        if (atk != 0.0) attrs.add(
            AttrData(
                res.getString(
                    R.string.atk
                ), atk
            )
        )
        if (magicStr != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.magic_str),
                magicStr
            )
        )
        if (def != 0.0) attrs.add(
            AttrData(
                res.getString(
                    R.string.def
                ), def
            )
        )
        if (magicDef != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.magic_def),
                magicDef
            )
        )
        if (physicalCritical != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.p_critical),
                physicalCritical
            )
        )
        if (magicCritical != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.m_critical),
                magicCritical
            )
        )
        if (waveHpRecovery != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.hp_recovery),
                waveHpRecovery
            )
        )
        if (waveEnergyRecovery != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.tp_recovery),
                waveEnergyRecovery
            )
        )
        if (dodge != 0.0) attrs.add(
            AttrData(
                res.getString(
                    R.string.dodge
                ), dodge
            )
        )
        if (physicalPenetrate != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.p_penetrate),
                physicalPenetrate
            )
        )
        if (magicPenetrate != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.m_penetrate),
                magicPenetrate
            )
        )
        if (lifeSteal != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.hp_steal),
                lifeSteal
            )
        )
        if (hpRecoveryRate != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.hp_recovery_rate),
                hpRecoveryRate
            )
        )
        if (energyRecoveryRate != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.tp_recovery_rate),
                energyRecoveryRate
            )
        )
        if (energyReduceRate != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.tp_reduce_rate),
                energyReduceRate
            )
        )
        if (accuracy != 0.0) attrs.add(
            AttrData(
                res.getString(R.string.accuracy),
                accuracy
            )
        )
        return attrs
    }
}


