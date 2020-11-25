package cn.wthee.pcrtool.data.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//角色属性状态
@Entity(tableName = "unit_enemy_data")
data class EnemyDataJP(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") var unit_id: Int = 0,
    @ColumnInfo(name = "unit_name") var unit_name: String = "",
    @ColumnInfo(name = "prefab_id") var prefab_id: Int = 0,
    @ColumnInfo(name = "motion_type") var motion_type: Int = 0,
    @ColumnInfo(name = "se_type") var se_type: Int = 0,
    @ColumnInfo(name = "move_speed") var move_speed: Int = 0,
    @ColumnInfo(name = "search_area_width") var search_area_width: Int = 0,
    @ColumnInfo(name = "atk_type") var atk_type: Int = 0,
    @ColumnInfo(name = "normal_atk_cast_time") var normal_atk_cast_time: Double = 0.0,
    @ColumnInfo(name = "cutin") var cutin: Int = 0,
    @ColumnInfo(name = "visual_change_flag") var visual_change_flag: Int = 0,
    @ColumnInfo(name = "comment") var comment: String = "",
    //jp
    @ColumnInfo(name = "cutin_star6") var cutinStar6: Int = 0,
) : Serializable