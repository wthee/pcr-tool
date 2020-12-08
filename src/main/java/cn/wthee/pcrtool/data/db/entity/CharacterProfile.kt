package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//角色基本资料
@Entity(tableName = "unit_profile")
data class CharacterProfile(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") val id: Int,
    @ColumnInfo(name = "unit_name") val name: String,
    @ColumnInfo(name = "age") val age: String,
    @ColumnInfo(name = "guild") val guild: String,
    @ColumnInfo(name = "race") val race: String,
    @ColumnInfo(name = "height") val height: String,
    @ColumnInfo(name = "weight") val weight: String,
    @ColumnInfo(name = "birth_month") val birthMonth: String,
    @ColumnInfo(name = "birth_day") val birthDay: String,
    @ColumnInfo(name = "blood_type") val bloodType: String,
    @ColumnInfo(name = "favorite") val favorite: String,
    @ColumnInfo(name = "voice") val voice: String,
    @ColumnInfo(name = "voice_id") val voiceId: Int,
    @ColumnInfo(name = "catch_copy") val catchCopy: String,
    @ColumnInfo(name = "self_text") val selfText: String,
    @ColumnInfo(name = "guild_id") val guildId: String
) : Serializable


