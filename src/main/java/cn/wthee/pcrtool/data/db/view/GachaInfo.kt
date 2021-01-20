package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 * 卡池记录
 */
data class GachaInfo(
    @ColumnInfo(name = "gacha_id") val gacha_id: Int,
    @ColumnInfo(name = "gacha_name") val gacha_name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
    @ColumnInfo(name = "unit_ids") val unitIds: String
) {

    /**
     * 获取卡池描述
     */
    fun getDesc() = description.replace("\\n", "\n")
}