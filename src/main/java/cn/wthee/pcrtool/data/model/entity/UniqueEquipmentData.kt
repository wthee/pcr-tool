package cn.wthee.pcrtool.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EquipmentAttr
import cn.wthee.pcrtool.utils.Constants.UNKNOW_EQUIP_ID
import java.io.Serializable

@Entity(tableName = "unique_equipment_data")
class UniqueEquipmentData(
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
) : Serializable


