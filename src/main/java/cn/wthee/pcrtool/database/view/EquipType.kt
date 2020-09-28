package cn.wthee.pcrtool.database.view

import androidx.room.ColumnInfo

data class EquipType(
    @ColumnInfo(name = "type") val type: String
)