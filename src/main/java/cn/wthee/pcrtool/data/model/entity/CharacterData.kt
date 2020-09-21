package cn.wthee.pcrtool.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//角色属性
@Entity(tableName = "unit_data")
data class CharacterData(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") val dataId: Int,
    @ColumnInfo(name = "unit_name") val name: String,
    @ColumnInfo(name = "kana") val kana: String,
    @ColumnInfo(name = "prefab_id") val prefabId: Int,
    @ColumnInfo(name = "rarity") val rarity: Int,
    @ColumnInfo(name = "motion_type") val motion_type: Int,
    @ColumnInfo(name = "se_type") val seType: Int,
    @ColumnInfo(name = "move_speed") val moveSpeed: Int,
    @ColumnInfo(name = "search_area_width") val position: Int,
    @ColumnInfo(name = "atk_type") val atkType: Int,
    @ColumnInfo(name = "normal_atk_cast_time") val atkTime: Double,
    @ColumnInfo(name = "cutin_1") val cutin1: Int,
    @ColumnInfo(name = "cutin_2") val cutin2: Int,
    @ColumnInfo(name = "guild_id") val guildId: Int,
    @ColumnInfo(name = "exskill_display") val exSkillDisplay: Int,
    @ColumnInfo(name = "comment") val comment: String,
    @ColumnInfo(name = "only_disp_owned") val onlyDispOwned: Int,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
) : Serializable {
    fun getFixedId() = dataId + 30
}