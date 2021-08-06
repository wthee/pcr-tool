package cn.wthee.pcrtool.data.db.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 角色类型
 */
@Entity(tableName = "chara_identity")
data class CharacterTypeJP(
    @PrimaryKey
    @ColumnInfo(name = "unit_id") val unit_id: Int,
    @ColumnInfo(name = "chara_type") val chara_type: Int,
    //JP
    @ColumnInfo(name = "chara_type_2") val chara_type_2: Int,
    @ColumnInfo(name = "chara_type_3") val chara_type_3: Int,

)
