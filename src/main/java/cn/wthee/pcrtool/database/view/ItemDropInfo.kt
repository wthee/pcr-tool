package cn.wthee.pcrtool.database.view

import androidx.room.ColumnInfo
import java.io.Serializable

data class ItemDropInfo(
    @ColumnInfo(name = "quest_id") val quest_id: Int,
    @ColumnInfo(name = "quest_name") val quest_name: String,
    @ColumnInfo(name = "item_id") val item_id: Int,
    @ColumnInfo(name = "item_name") val item_name: String,
) : Serializable {
    fun getNum() = quest_name.split(" ")[1]

    fun getName() = quest_name.split(" ")[0]

}