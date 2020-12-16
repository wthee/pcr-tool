package cn.wthee.pcrtool.data.db.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//角色属性
@Entity(tableName = "unit_data")
data class CharacterDataJP(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") var dataId: Int = 0,
    @ColumnInfo(name = "unit_name") var name: String = "",
    @ColumnInfo(name = "kana") var kana: String = "",
    @ColumnInfo(name = "prefab_id") var prefabId: Int = 0,
    @ColumnInfo(name = "rarity") var rarity: Int = 0,
    @ColumnInfo(name = "motion_type") var motion_type: Int = 0,
    @ColumnInfo(name = "se_type") var seType: Int = 0,
    @ColumnInfo(name = "move_speed") var moveSpeed: Int = 0,
    @ColumnInfo(name = "search_area_width") var position: Int = 0,
    @ColumnInfo(name = "atk_type") var atkType: Int = 0,
    @ColumnInfo(name = "normal_atk_cast_time") var atkTime: Double = 0.0,
    @ColumnInfo(name = "cutin_1") var cutin1: Int = 0,
    @ColumnInfo(name = "cutin_2") var cutin2: Int = 0,
    @ColumnInfo(name = "guild_id") var guildId: Int = 0,
    @ColumnInfo(name = "exskill_display") var exSkillDisplay: Int = 0,
    @ColumnInfo(name = "comment") var comment: String = "",
    @ColumnInfo(name = "only_disp_owned") var onlyDispOwned: Int = 0,
    @ColumnInfo(name = "start_time") var startTime: String = "",
    @ColumnInfo(name = "end_time") var endTime: String = "",
    //jp
    @ColumnInfo(name = "is_limited") var isLimited: Int = 0,
    @ColumnInfo(name = "cutin1_star6") var cutin1Star6: Int = 0,
    @ColumnInfo(name = "cutin2_star6") var cutin2Star6: Int = 0
) : Serializable