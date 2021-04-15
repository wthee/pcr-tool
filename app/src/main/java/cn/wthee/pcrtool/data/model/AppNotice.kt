package cn.wthee.pcrtool.data.model

import java.io.Serializable

/**
 * 应用通知内容
 */
data class AppNotice(
    val date: String,
    val file_url: String,
    val id: Int,
    val img_url: String,
    val message: String,
    val title: String,
    val type: Int,
    val url: String
) : Serializable