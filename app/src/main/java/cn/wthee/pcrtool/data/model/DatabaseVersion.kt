package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.utils.Constants.SQLITE_VERSION

/**
 * 数据库远程版本信息
 */
data class DatabaseVersion(
    val TruthVersion: String,
    val hash: String,
    val desc:String
) {
    override fun toString(): String {
        return "$TruthVersion/$hash/${SQLITE_VERSION}"
    }
}