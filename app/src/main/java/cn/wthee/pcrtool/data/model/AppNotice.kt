package cn.wthee.pcrtool.data.model

import kotlinx.serialization.Serializable


/**
 * 应用通知内容
 */
@Serializable
data class AppNotice(
    val date: String = "",
    val file_url: String = "",
    var id: Int = -1,
    val img_url: String = "",
    val message: String = "",
    val title: String = "",
    val type: Int = -1,
    var url: String = ""
)