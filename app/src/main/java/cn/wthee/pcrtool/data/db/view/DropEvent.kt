package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.utils.fillZero

/**
 * 活动信息
 */
data class DropEvent(
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "value") val value: Int,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
) {

    /**
     * 格式化时间
     */
    fun getFixedStartTime(): String {
        val list = startTime.split(" ")[0].split("/")
        val hms = startTime.substring(startTime.length - 8, startTime.length)
        return "${list[0]}/${list[1].fillZero()}/${list[2].fillZero()} ${hms}"
    }

    fun getFixedEndTime(): String {
        val list = endTime.split(" ")[0].split("/")
        val hms = startTime.substring(startTime.length - 8, startTime.length)
        return "${list[0]}/${list[1].fillZero()}/${list[2].fillZero()} ${hms}"
    }

    /**
     * 去零
     */
    fun getFixedValue() = if (value % 1000 != 0) {
        (value / 1000f).toString()
    } else {
        (value / 1000).toString()
    }

}

data class CalendarEventData(
    val title: String,
    val info: String,
    val colorId: Int = R.color.black,
)