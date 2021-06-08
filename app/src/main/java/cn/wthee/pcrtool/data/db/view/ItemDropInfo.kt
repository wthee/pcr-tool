package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 * 道具掉落视图
 */
data class ItemDropInfo(
    @ColumnInfo(name = "quest_id") val quest_id: Int,
    @ColumnInfo(name = "quest_name") val quest_name: String,
    @ColumnInfo(name = "item_id") val item_id: Int,
    @ColumnInfo(name = "item_name") val item_name: String,
) {

    /**
     * 副本编号
     */
    fun getNum() = quest_name.split(" ")[1]

    /**
     * 副本名
     */
    fun getName() = quest_name.split(" ")[0]

}