package cn.wthee.pcrtool.database.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.adapters.UnitData

data class GachaInfo(
    @ColumnInfo(name = "gacha_id") val gacha_id: Int,
    @ColumnInfo(name = "gacha_name") val gacha_name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
    @ColumnInfo(name = "unit_ids") val unitIds: String,
    @ColumnInfo(name = "unit_names") val unitNames: String,
) {

    fun getUnits(): ArrayList<UnitData> {
        val units = arrayListOf<UnitData>()
        val names = unitNames.split("-")
        val ids = unitIds.split("-")
        ids.forEachIndexed { index, id ->
            units.add(UnitData(id.toInt(), names[index]))
        }
        return units
    }

    fun getDesc() = "- " + description.replace("\\n", "\n- ")
}