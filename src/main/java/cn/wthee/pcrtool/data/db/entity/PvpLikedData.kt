package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pvp_like")
data class PvpLikedData(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "atks") val atks: String,
    @ColumnInfo(name = "defs") val defs: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "region") val region: Int,
) {
    fun getIds(): List<Int> {
        val atkIds = atks.split("-")
        val defIds = defs.split("-")
        val ids = arrayListOf<Int>()
        (atkIds + defIds).forEach {
            if (it.isNotEmpty()) {
                ids.add(it.toInt())
            }
        }
        return ids
    }
}
