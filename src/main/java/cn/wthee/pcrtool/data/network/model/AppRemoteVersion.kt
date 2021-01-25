package cn.wthee.pcrtool.data.network.model

/**
 * 应用远程版本信息
 */
data class AppRemoteVersion(
    val url: String,
    val versionCode: Int,
    val versionName: String,
    val content: String
)