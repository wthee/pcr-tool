package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.BuildConfig

/**
 * 数据库远程版本信息
 */
data class DatabaseVersion(
    val TruthVersion: String,
    val hash: String
) {
    override fun toString(): String {
        return "$TruthVersion/$hash/${BuildConfig.SQLITE_VERSION}"
    }
}