package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


/**
 * 角色现实资料
 */
@Entity(tableName = "actual_unit_background")
data class CharacterActualData(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") val id: Int,
    @ColumnInfo(name = "unit_name") val name: String,
    @ColumnInfo(name = "bg_id") val bgId: Int,
    @ColumnInfo(name = "face_type") val faceType: Int
) : Serializable
