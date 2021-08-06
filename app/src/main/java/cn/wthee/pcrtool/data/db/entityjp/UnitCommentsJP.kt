package cn.wthee.pcrtool.data.db.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 角色交流文本
 */
@Entity(
    tableName = "unit_comments",
    indices = [Index(
        value = arrayOf("unit_id"),
        unique = false,
        name = "unit_comments_0_unit_id"
    ), Index(
        value = arrayOf("unit_id", "use_type"),
        unique = false,
        name = "unit_comments_0_unit_id_1_use_type"
    )]
)
data class UnitCommentsJP(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "unit_id")
    val unitId: Int,
    @ColumnInfo(name = "use_type")
    val useType: Int,
    @ColumnInfo(name = "voice_id")
    val voiceId: Int,
    @ColumnInfo(name = "face_id")
    val faceId: Int,
    @ColumnInfo(name = "change_time")
    val changeTime: Double,
    @ColumnInfo(name = "change_face")
    val changeFace: Int,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "all_comments_flag")
    val allCommentsFlag: Int,
    @ColumnInfo(name = "target_unit_id")
    val targetUnitId: Int,
    @ColumnInfo(name = "face_id_2")
    val faceId2: Int,
    @ColumnInfo(name = "change_time_2")
    val changeTime2: Double,
    @ColumnInfo(name = "change_face_2")
    val changeFace2: Int,
)