package cn.wthee.pcrtool.data.model

import com.google.gson.annotations.SerializedName

data class BilibiliNewsData(
    val code: Int,
    val `data`: NewsDetailData,
    val gameInfo: GameInfo,
    @SerializedName("request-id")
    val requestId: String
)

data class GameInfo(
    val androidLink: String,
    val forum: String,
    val gameExtensionId: Int,
    val gameType: Int,
    val image: String,
    val name: String,
    val website: String
)

data class NewsDetailData(
    val author: String,
    val commentId: String,
    val content: String,
    val gameExtensionId: Int,
    val id: Int,
    val modifyTime: String,
    val site: String,
    val title: String,
    val typeId: String,
    val typeName: String
)
