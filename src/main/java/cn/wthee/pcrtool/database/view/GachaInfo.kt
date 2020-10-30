package cn.wthee.pcrtool.database.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.Constants

data class GachaInfo(
    @ColumnInfo(name = "gacha_id") val gacha_id: Int,
    @ColumnInfo(name = "gacha_name") val gacha_name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
    @ColumnInfo(name = "unit_ids") val unitIds: String,
    @ColumnInfo(name = "unit_names") val unitNames: String,
) {
    fun getIds(): List<Int> {
        val ids = mutableListOf<Int>()
        val idStrs = unitIds.split("-")
        idStrs.forEach {
            ids.add(it.toInt())
        }
        return ids
    }

    fun getAllUrls(): List<String> {
        val urls = mutableListOf<String>()
        val ids = unitIds.split("-")
        ids.forEach {
            urls.add(Constants.UNIT_ICON_URL + (it.toInt() + 30) + Constants.WEBP)
        }
        return urls
    }

    fun getNames() = unitNames.split("-")

    fun getDesc() = "- " + description.replace("\\n", "\n- ")
}