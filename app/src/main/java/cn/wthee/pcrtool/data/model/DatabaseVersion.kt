package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.utils.Constants.SQLITE_VERSION
import kotlinx.serialization.Serializable

/**
 * 数据库远程版本信息
 */
@Serializable
data class DatabaseVersion(
    val truthVersion: String,
    val hash: String,
    val desc:String,
    val time:String
) {
    override fun toString(): String {
        return "$truthVersion/$hash/${SQLITE_VERSION}"
    }
}