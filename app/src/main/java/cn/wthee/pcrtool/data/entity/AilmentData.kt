package cn.wthee.pcrtool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ailment_data")
data class AilmentData(
    @PrimaryKey
    @ColumnInfo(name = "ailment_id") val ailmentId: Int,
    @ColumnInfo(name = "ailment_action") val ailmentAction: Int,
    @ColumnInfo(name = "ailment_detail_1") val ailmentDetail: Int,
    @ColumnInfo(name = "ailment_name") val ailmentName: String,
)
