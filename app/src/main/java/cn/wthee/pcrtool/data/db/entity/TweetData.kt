package cn.wthee.pcrtool.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 推特信息
 */
@Entity(tableName = "tweet")
data class TweetData(
    @PrimaryKey
    val id: String = "",
    val date: String = "2021-01-01 12:00:00",
    val tweet: String = "",
    val photos: String = ""
)