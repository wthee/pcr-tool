package cn.wthee.pcrtool.data.model

import java.io.Serializable

/**
 * 应用通知内容
 */
data class AppNotice(
    val date: String = "",
    val file_url: String = "",
    var id: Int = -1,
    val img_url: String = "",
    val message: String = "",
    val title: String = "1.1.0",
    val type: Int = -1,
    var url: String = ""
) : Serializable