package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.data.db.view.BirthdayData
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.EventData
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.data.db.view.endTime
import cn.wthee.pcrtool.data.db.view.startTime


/**
 * 日常相关排序
 */
fun <T> compareAllTypeEvent(today: String? = null) = Comparator<T> { o1, o2 ->
    var sd1 = ""
    var ed1 = ""
    var sd2 = ""
    var ed2 = ""

    when (o1) {
        is CalendarEvent -> {
            sd1 = o1.startTime.formatTime
            ed1 = o1.endTime.formatTime
            sd2 = (o2 as CalendarEvent).startTime.formatTime
            ed2 = (o2 as CalendarEvent).endTime.formatTime
        }

        is GachaInfo -> {
            sd1 = o1.startTime.formatTime
            ed1 = o1.endTime.formatTime
            sd2 = (o2 as GachaInfo).startTime.formatTime
            ed2 = (o2 as GachaInfo).endTime.formatTime
        }

        is EventData -> {
            sd1 = o1.startTime.formatTime
            ed1 = o1.endTime.formatTime
            sd2 = (o2 as EventData).startTime.formatTime
            ed2 = (o2 as EventData).endTime.formatTime
        }

        is BirthdayData -> {
            sd2 = o1.startTime.formatTime
            ed2 = o1.endTime.formatTime
            sd1 = (o2 as BirthdayData).startTime.formatTime
            ed1 = (o2 as BirthdayData).endTime.formatTime
        }
    }
    compareDate(today, sd1, ed1, sd2, ed2)
}


private fun compareDate(
    today: String? = null,
    sd1: String,
    ed1: String,
    sd2: String,
    ed2: String
) =
    if (today == null) {
        when {
            sd1.second(sd2) > 0 -> -1
            sd1.second(sd2) == 0L -> {
                ed2.compareTo(ed1)
            }
            else -> 1
        }
    } else {
        if (today.second(sd1) > 0 && ed1.second(today) > 0) {
            if (today.second(sd2) > 0 && ed2.second(today) > 0) {
                //都是进行中，比较结束时间
                ed1.compareTo(ed2)
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
