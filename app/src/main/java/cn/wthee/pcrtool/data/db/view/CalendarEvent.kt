package cn.wthee.pcrtool.data.db.view

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo

/**
 * 活动信息
 */
data class CalendarEvent(
    @ColumnInfo(name = "type") val type: String = "31",
    @ColumnInfo(name = "value") val value: Int = 1500,
    @ColumnInfo(name = "start_time") val startTime: String = "2021-01-01 00:00:00",
    @ColumnInfo(name = "end_time") val endTime: String = "2021-01-07 00:00:00",
) {
    /*
     * 去零
     */
    fun getFixedValue() = if (value % 1000 != 0) {
        (value / 1000f)
    } else {
        (value / 1000).toFloat()
    }

}

data class CalendarEventData(
    val title: String,
    val multiple: Float,
    val info: String,
    val color: Color = Color.Unspecified,
)