package cn.wthee.pcrtool.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 官网公告
 */
@Entity(tableName = "news")
data class NewsTable(
    @PrimaryKey
    val id: String = "2-1",
    val title: String = "",
    val tags: String = "???",
    val url: String = "",
    val date: String = "2021-01-01"
) {

    fun getTag(): String {
        val tags = tags.split(",").filter {
            it.isNotEmpty() && it != "すべて"
        } as ArrayList<String>
        if (tags.size > 1) tags.remove("お知らせ")
        return when (tags[0]) {
            "アップデート" -> "更新"
            "系統", "メンテナンス" -> "系统"
            "お知らせ" -> "新闻"
            "活動", "イベント" -> "活动"
            "グッズ" -> "周边"
            else -> tags[0]
        }
    }


}

fun String.original() = replace('$', '/')

val String.region: Int
    get() {
        return when {
            this.contains("bilibili") -> 2
            this.contains(".tw") -> 3
            else -> 4
        }
    }

fun String.getNewsId(): String {
    val url = if (this.last() == '/') {
        this.substring(0, this.length - 1)
    } else {
        this
    }
    return url.substringAfterLast("/")
}