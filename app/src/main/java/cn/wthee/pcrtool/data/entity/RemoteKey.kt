package cn.wthee.pcrtool.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 公告分页
 */
@Entity(tableName = "remote_key")
data class RemoteKey(
    @PrimaryKey val repoId: String,
    val prevKey: Int?,
    val nextKey: Int?
)