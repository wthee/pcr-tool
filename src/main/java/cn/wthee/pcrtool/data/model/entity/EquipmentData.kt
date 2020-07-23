package cn.wthee.pcrtool.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EquipmentAttr
import cn.wthee.pcrtool.utils.Constants.UNKNOW_EQUIP_ID
import java.io.Serializable

@Entity(tableName = "equipment_data")
class EquipmentData(
    @PrimaryKey
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "equipment_name") val equipmentName: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "promotion_level") val promotionLevel: Int,
    @ColumnInfo(name = "craft_flg") val craftFlg: Int,
    @ColumnInfo(name = "equipment_enhance_point") val equipmentEnhancePoint: Int,
    @ColumnInfo(name = "sale_price") val salePrice: Int,
    @ColumnInfo(name = "require_level") val requireLevel: Int,
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
    @ColumnInfo(name = "enable_donation") val enableDonation: Int,
    @ColumnInfo(name = "accuracy") val accuracy: Double
) : Serializable {

    companion object {
        fun unknow() =
            EquipmentData(
                UNKNOW_EQUIP_ID,
                "？？？",
                "",
                0,
                0,
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
                0,
                0.0
            )

    }

    fun getDesc() = description.replace("\\n", "")

    private fun calcValue(value: Double): Int {
        val rate = if (promotionLevel > 1) 2 else 1
        return if (promotionLevel == 3 && value.toInt() % 3 != 0) {
            value.toInt() * 2 + 1
        } else {
            value.toInt() * rate
        }
    }

    fun getAttrs(): ArrayList<EquipmentAttr> {
        val attrs = arrayListOf<EquipmentAttr>()
        val res = MyApplication.getContext().resources

        if (hp != 0.0) attrs.add(
            EquipmentAttr(
                "HP",
                calcValue(hp)
            )
        )
        if (atk != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(
                    R.string.atk
                ), calcValue(atk)
            )
        )
        if (magicStr != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.magic_str),
                calcValue(magicStr)
            )
        )
        if (def != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(
                    R.string.def
                ), calcValue(def)
            )
        )
        if (magicDef != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.magic_def),
                calcValue(magicDef)
            )
        )
        if (physicalCritical != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.p_critical),
                calcValue(physicalCritical)
            )
        )
        if (magicCritical != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.m_critical),
                calcValue(magicCritical)
            )
        )
        if (waveHpRecovery != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.hp_recovery),
                calcValue(waveHpRecovery)
            )
        )
        if (waveEnergyRecovery != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.tp_recovery),
                calcValue(waveEnergyRecovery)
            )
        )
        if (dodge != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(
                    R.string.dodge
                ), calcValue(dodge)
            )
        )
        if (physicalPenetrate != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.p_penetrate),
                calcValue(physicalPenetrate)
            )
        )
        if (magicPenetrate != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.m_penetrate),
                calcValue(magicPenetrate)
            )
        )
        if (lifeSteal != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.hp_steal),
                calcValue(lifeSteal)
            )
        )
        if (hpRecoveryRate != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.hp_recovery_rate),
                calcValue(hpRecoveryRate)
            )
        )
        if (energyRecoveryRate != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.tp_recovery_rate),
                calcValue(energyRecoveryRate)
            )
        )
        if (energyReduceRate != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.tp_reduce_rate),
                calcValue(energyReduceRate)
            )
        )
        if (accuracy != 0.0) attrs.add(
            EquipmentAttr(
                res.getString(R.string.accuracy),
                calcValue(accuracy)
            )
        )
        return attrs
    }
}


