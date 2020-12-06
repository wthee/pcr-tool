package cn.wthee.pcrtool.data.model

data class AppRemoteVersion(
    val url: String,
    val versionCode: Int,
    val versionName: String,
    val content: String
)