package cn.wthee.pcrtool.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 日服从接口获取
 * 国服官方 https://manga.bilibili.com/detail/mc28174
 */
@Entity(tableName = "comic")
data class ComicData(
    @PrimaryKey
    val id: Int = -1,
    val title: String = "???",
    val url: String = "",
    val date: String
)
