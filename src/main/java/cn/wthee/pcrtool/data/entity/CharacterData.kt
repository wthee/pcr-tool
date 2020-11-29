package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//角色属性
@Entity(tableName = "unit_data")
data class CharacterData(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") var dataId: Int,
    @ColumnInfo(name = "unit_name") var name: String,
    @ColumnInfo(name = "kana") var kana: String,
    @ColumnInfo(name = "prefab_id") var prefabId: Int,
    @ColumnInfo(name = "rarity") var rarity: Int,
    @ColumnInfo(name = "motion_type") var motion_type: Int,
    @ColumnInfo(name = "se_type") var seType: Int,
    @ColumnInfo(name = "move_speed") var moveSpeed: Int,
    @ColumnInfo(name = "search_area_width") var position: Int,
    @ColumnInfo(name = "atk_type") var atkType: Int,
    @ColumnInfo(name = "normal_atk_cast_time") var atkTime: Double,
    @ColumnInfo(name = "cutin_1") var cutin1: Int,
    @ColumnInfo(name = "cutin_2") var cutin2: Int,
    @ColumnInfo(name = "guild_id") var guildId: Int,
    @ColumnInfo(name = "exskill_display") var exSkillDisplay: Int,
    @ColumnInfo(name = "comment") var comment: String,
    @ColumnInfo(name = "only_disp_owned") var onlyDispOwned: Int,
    @ColumnInfo(name = "start_time") var startTime: String,
    @ColumnInfo(name = "end_time") var endTime: String,
) : Serializable