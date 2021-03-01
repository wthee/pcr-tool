package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 角色类型
 */
@Entity(tableName = "chara_identity")
data class CharacterType(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") val unit_id: Int,
    @ColumnInfo(name = "chara_type") val chara_type: Int,
)
