package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "chara_story_status")
data class CharacterStoryStatus(
    @PrimaryKey
    @ColumnInfo(name = "story_id") val story_id: Int,
    @ColumnInfo(name = "unlock_story_name") val unlock_story_name: String,
    @ColumnInfo(name = "status_type_1") val status_type_1: Int,
    @ColumnInfo(name = "status_rate_1") val status_rate_1: Int,
    @ColumnInfo(name = "status_type_2") val status_type_2: Int,
    @ColumnInfo(name = "status_rate_2") val status_rate_2: Int,
    @ColumnInfo(name = "status_type_3") val status_type_3: Int,
    @ColumnInfo(name = "status_rate_3") val status_rate_3: Int,
    @ColumnInfo(name = "status_type_4") val status_type_4: Int,
    @ColumnInfo(name = "status_rate_4") val status_rate_4: Int,
    @ColumnInfo(name = "status_type_5") val status_type_5: Int,
    @ColumnInfo(name = "status_rate_5") val status_rate_5: Int,
    @ColumnInfo(name = "chara_id_1") val chara_id_1: Int,
    @ColumnInfo(name = "chara_id_2") val chara_id_2: Int,
    @ColumnInfo(name = "chara_id_3") val chara_id_3: Int,
    @ColumnInfo(name = "chara_id_4") val chara_id_4: Int,
    @ColumnInfo(name = "chara_id_5") val chara_id_5: Int,
    @ColumnInfo(name = "chara_id_6") val chara_id_6: Int,
    @ColumnInfo(name = "chara_id_7") val chara_id_7: Int,
    @ColumnInfo(name = "chara_id_8") val chara_id_8: Int,
    @ColumnInfo(name = "chara_id_9") val chara_id_9: Int,
    @ColumnInfo(name = "chara_id_10") val chara_id_10: Int,
) : Serializable