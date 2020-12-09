package cn.wthee.pcrtool.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//官网公告
@Entity(tableName = "news")
data class NewsTable(
    @PrimaryKey
    val id: String,
    val title: String,
    val tags: String,
    val url: String,
    val date: String
) : Serializable {

    fun getTagList() = tags.split(",").filter {
        it.isNotEmpty() && it != "すべて"
    } as ArrayList<String>

    fun getTrueId() = id.split("-")[1].toInt()
}