package cn.wthee.pcrtool.data.model

import java.io.Serializable

data class NewsData(
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

