package cn.wthee.pcrtool.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * 漫画信息
 */
@Serializable
@Entity(tableName = "comic")
data class ComicData(
    @PrimaryKey
    val id: Int = 0,
    val title: String = "",
    val url: String = "",
    val date: String = "2021-01-01 12:00:00",
)