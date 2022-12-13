package cn.wthee.pcrtool.data.model


/**
 * 网站信息
 */
data class WebsiteGroupData(
    val type: Int,
    val websiteList: List<WebsiteData>
)

data class WebsiteData(
    val id: Int,
    val author: String,
    val icon: String,
    val title: String,
    val summary: String,
    val url: String,
    val orderId: Int
)