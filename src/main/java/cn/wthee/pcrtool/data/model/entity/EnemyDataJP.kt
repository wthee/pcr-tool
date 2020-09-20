package cn.wthee.pcrtool.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//角色属性状态
@Entity(tableName = "unit_enemy_data")
data class EnemyDataJP(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") val unit_id: Int,
    @ColumnInfo(name = "unit_name") val unit_name: String,
    @ColumnInfo(name = "prefab_id") val prefab_id: Int,
    @ColumnInfo(name = "motion_type") val motion_type: Int,
    @ColumnInfo(name = "se_type") val se_type: Int,
    @ColumnInfo(name = "move_speed") val move_speed: Int,
    @ColumnInfo(name = "search_area_width") val search_area_width: Int,
    @ColumnInfo(name = "atk_type") val atk_type: Int,
    @ColumnInfo(name = "normal_atk_cast_time") val normal_atk_cast_time: Double,
    @ColumnInfo(name = "cutin") val cutin: Int,
    @ColumnInfo(name = "visual_change_flag") val visual_change_flag: Int,
    @ColumnInfo(name = "comment") val comment: String,
    //jp
    @ColumnInfo(name = "cutin_star6") val cutinStar6: Int,
) : Serializable {
    fun getFixedComment() = comment.replace("\\n", "，")

    fun getTruePrefabId(): Int {
        val trueId = prefab_id + 10
        return trueId
    }
}