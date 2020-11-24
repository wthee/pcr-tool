package cn.wthee.pcrtool.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class NewsData(
    val status: Int,
    val data: List<News>
) : Serializable

data class News(
    val id: Int,
    val title: String,
    val tag: List<String>,
    val url: String,
    val date: String,
) : Serializable {
    fun getTags(): String {
        var tags = ""
        tag.forEach {
            tags += "$it,"
        }
        return tags
    }
}

@Entity(tableName = "news")
data class NewsTable(
    @PrimaryKey
    val id: String,
    val title: String,
    val tags: String,
    val url: String,
    val date: String
) : Serializable
