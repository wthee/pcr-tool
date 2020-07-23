package cn.wthee.pcrtool.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//角色羁绊Rank提升文本
@Entity(tableName = "character_love_rankup_text")
data class CharacterLoveRank(
    @PrimaryKey
    @ColumnInfo(name = "chara_id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "love_level") val loveLevel: Int,
    @ColumnInfo(name = "scale") val scale: Double,
    @ColumnInfo(name = "position_x") val positionX: Int,
    @ColumnInfo(name = "position_y") val positionY: Int,
    @ColumnInfo(name = "voice_id_1") val voiceId1: Int,
    @ColumnInfo(name = "face_1") val face1: Int,
    @ColumnInfo(name = "serif_1") val serif1: String,
    @ColumnInfo(name = "voice_id_2") val voiceId2: Int,
    @ColumnInfo(name = "face_2") val face2: Int,
    @ColumnInfo(name = "serif_2") val serif2: String,
    @ColumnInfo(name = "voice_id_3") val voiceId3: Int,
    @ColumnInfo(name = "face_3") val face3: Int,
    @ColumnInfo(name = "serif_3") val serif3: String
) : Serializable
