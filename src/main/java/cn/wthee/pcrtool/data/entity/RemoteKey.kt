package cn.wthee.pcrtool.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_key")
data class RemoteKey(
    @PrimaryKey val repoId: String,
    val prevKey: Int?,
    val nextKey: Int?
)