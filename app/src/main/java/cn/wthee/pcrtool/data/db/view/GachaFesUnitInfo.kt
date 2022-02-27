package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 卡池角色信息
 */
data class GachaFesUnitInfo(
    @ColumnInfo(name = "unit_ids") val unitIds: String,
    @ColumnInfo(name = "unit_names") val unitNames: String
) {
    fun getIds() = unitIds.intArrayList

    fun getNames() = unitNames.split('-')
}