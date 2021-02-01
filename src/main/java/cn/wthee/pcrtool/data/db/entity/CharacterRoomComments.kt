package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "room_unit_comments",
    indices = [Index(
        value = arrayOf("unit_id"),
        unique = false,
        name = "room_unit_comments_0_unit_id"
    )],
    primaryKeys = ["unit_id", "trigger", "voice_id", "time"]
)
data class CharacterRoomComments(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "unit_id") val unit_id: Int,
    @ColumnInfo(name = "trigger") val trigger: Int,
    @ColumnInfo(name = "voice_id") val voice_id: Int,
    @ColumnInfo(name = "beloved_step") val beloved_step: Int,
    @ColumnInfo(name = "time") val time: Int,
    @ColumnInfo(name = "face_id") val face_id: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "insert_word_type") val insert_word_type: Int
)
