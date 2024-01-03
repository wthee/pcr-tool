package cn.wthee.pcrtool.data.model

import kotlinx.serialization.Serializable


/**
 * 网站信息
 */
@Serializable
data class WebsiteGroupData(
    val type: Int,
    val typeName: String,
    var websiteList: List<WebsiteData>
)

@Serializable
data class WebsiteData(
    val id: Int = 0,
    val author: String? = "",
    val icon: String = "",
    val title: String = "",
    val summary: String = "",
    val url: String = "",
    val region: Int = 0,
    val browserType: Int = 0,
)