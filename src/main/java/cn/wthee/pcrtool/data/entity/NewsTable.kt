package cn.wthee.pcrtool.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


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
        it.isNotEmpty()
    }


    fun getTrueId() = id.split("-")[1].toInt()
}