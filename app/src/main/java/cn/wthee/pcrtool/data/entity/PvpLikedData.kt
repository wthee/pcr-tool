package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 竞技场收藏
 */
@Entity(tableName = "pvp_like")
data class PvpLikedData(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "atks") val atks: String,
    @ColumnInfo(name = "defs") val defs: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "region") val region: Int,
    // 0:默认 1:用户添加
    @ColumnInfo(name = "type") val type: Int = 0,
) {
    fun getIds() = atks.intArrayList() + defs.intArrayList()
}
