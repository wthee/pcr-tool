package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 竞技场历史记录
 */
@Entity(tableName = "pvp_history")
data class PvpHistoryData(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "defs") val defs: String,
    @ColumnInfo(name = "date") val date: String
) {
    fun getDefIds() = defs.split("@")[1].intArrayList

}
