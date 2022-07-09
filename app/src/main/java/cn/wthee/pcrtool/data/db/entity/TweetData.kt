package cn.wthee.pcrtool.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.utils.deleteSpace

/**
 * 推特信息
 */
@Entity(tableName = "tweet")
data class TweetData(
    @PrimaryKey
    val id: String = "",
    val date: String = "2021-01-01 12:00:00",
    val tweet: String = "",
    val photos: String = "",
    val urls: String = "",
    val link: String = "",
) {
    fun getImageList(): ArrayList<String> {
        val urls = arrayListOf<String>()
        photos.split(",").forEach {
            if (it != "") {
                urls.add(it)
            }
        }
        return urls
    }

    fun getUrlList() = urls.split(",").filter { it != "" }

    fun getFormatTweet(): String {
        return tweet.substringBefore("http").deleteSpace.substringBeforeLast("\n")
    }
}