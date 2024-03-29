package cn.wthee.pcrtool.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.data.enums.NewsType
import kotlinx.serialization.Serializable

/**
 * 官网公告，api和本地数据公用
 */
@Serializable
@Entity(tableName = "news")
data class NewsTable(
    @PrimaryKey
    val id: Int = -1,
    val sourceId: String = "0",
    val title: String = "",
    val tags: String = "???",
    val url: String = "",
    val date: String = "2021-01-01",
    val region: Int = 2
) {

    fun getTag(): NewsType {
        val tags = tags.split(",").filter {
            it.isNotEmpty() && it != "すべて"
        } as ArrayList<String>
        if (tags.size > 1) tags.remove("お知らせ")
        return when (tags[0]) {
            "更新", "アップデート" -> NewsType.UPDATE
            "系統", "メンテナンス" -> NewsType.SYSTEM
            "活动", "活動", "イベント" -> NewsType.EVENT
            "グッズ" -> NewsType.SHOP
            "本地化笔记" -> NewsType.LOCAL
            else -> NewsType.NEWS
        }
    }


}

val String.region: Int
    get() {
        return when {
            this.contains("bilibili") -> 2
            this.contains(".tw") -> 3
            else -> 4
        }
    }