package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * 角色基本属性
 */
@Entity(tableName = "unit_enemy_data")
data class UnitEnemyData(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") var dataId: Int,
    @ColumnInfo(name = "unit_name") var name: String,
    @ColumnInfo(name = "prefab_id") var prefabId: Int,
    @ColumnInfo(name = "motion_type") var motion_type: Int,
    @ColumnInfo(name = "se_type") var seType: Int,
    @ColumnInfo(name = "move_speed") var moveSpeed: Int,
    @ColumnInfo(name = "search_area_width") var position: Int,
    @ColumnInfo(name = "atk_type") var atkType: Int,
    @ColumnInfo(name = "normal_atk_cast_time") var atkTime: Double,
    @ColumnInfo(name = "cutin") var cutin1: Int,
    @ColumnInfo(name = "cutin_star6") var cutin1Star6: Int,
    @ColumnInfo(name = "visual_change_flag") var visualChangeFlag: Int,
    @ColumnInfo(name = "comment") var comment: String,
) : Serializable