package cn.wthee.pcrtool.data.network.model

data class AppRemoteVersion(
    val url: String,
    val versionCode: Int,
    val versionName: String,
    val content: String
)