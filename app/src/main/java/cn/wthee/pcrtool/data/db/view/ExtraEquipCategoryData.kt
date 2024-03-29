package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * ex装备类型信息
 */
data class ExtraEquipCategoryData(
    @PrimaryKey
    @ColumnInfo(name = "category") val category: Int,
    @ColumnInfo(name = "category_name") val categoryName: String
)