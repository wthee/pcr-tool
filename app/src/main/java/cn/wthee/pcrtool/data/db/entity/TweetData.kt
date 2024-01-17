package cn.wthee.pcrtool.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * 推特信息
 */
@Serializable
@Entity(tableName = "tweet")
data class TweetData(
    @PrimaryKey
    val id: Int = 0,
    val sourceId: String = "",
    val date: String = "2021-01-01 12:00:00",
    val tweet: String = "",
    val photos: String = "",
    val urls: String? = "",
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

    /**
     * 处理文本格式
     */
    fun getFormatTweet(): String {
        return tweet.replace("【公主连结】", "")
            .replace("#公主连结#", "")
            .replace("#公主连结Re:Dive#", "")
            .replace("\n\n", "")
    }
}