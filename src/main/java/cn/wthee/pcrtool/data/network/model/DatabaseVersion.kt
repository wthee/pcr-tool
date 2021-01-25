package cn.wthee.pcrtool.data.network.model

/**
 * 数据库远程版本信息
 */
data class DatabaseVersion(
    val TruthVersion: String,
    val hash: String
)