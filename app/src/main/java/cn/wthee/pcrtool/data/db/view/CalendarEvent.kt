package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.second

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

/**
 * 排序
 */
fun compare(today: String) = Comparator<CalendarEvent> { o1, o2 ->
    val sd1 = o1.startTime.formatTime()
    val ed1 = o1.endTime.formatTime()
    val sd2 = o2.startTime.formatTime()
    val ed2 = o2.endTime.formatTime()
    if (today.second(sd1) > 0 && ed1.second(today) > 0) {
        if (today.second(sd2) > 0 && ed2.second(today) > 0) {
            //都是进行中，比较结束时间
            ed2.compareTo(ed1)
        } else {
            //o1进行中
            -1
        }
    } else {
        if (today.second(sd2) > 0 && ed2.second(today) > 0) {
            //o2进行中
            1
        } else {
            //不是进行中
            if (sd1.second(today) > 0) {
                if (sd2.second(today) > 0) {
                    //即将举行
                    sd1.compareTo(sd2)
                } else {
                    -1
                }
            } else {
                if (sd2.second(today) > 0) {
                    //即将举行
                    1
                } else {
                    sd2.compareTo(sd1)
                }
            }
        }
    }
}