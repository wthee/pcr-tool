package cn.wthee.pcrtool.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 假表，用于创建数据库
 */
@Entity(tableName = "fake")
data class FakeEntity(
    @PrimaryKey
    val id: Int
)