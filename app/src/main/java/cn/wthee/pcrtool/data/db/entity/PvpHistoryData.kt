package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 竞技场收藏
 */
@Entity(tableName = "pvp_history")
data class PvpHistoryData(
    @PrimaryKey
    @ColumnInfo(name = "defs") val defs: String,
    @ColumnInfo(name = "date") val date: String
) {
    fun getDefIds(): List<Int> {
        val list = arrayListOf<Int>()
        val ids = defs.split("@")[1].split("-")
        ids.forEachIndexed { _, id ->
            if (id != "") {
                list.add(id.toInt())
            }
        }
        return list
    }
}
