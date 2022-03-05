package cn.wthee.pcrtool.utils

import android.content.ContentValues
import android.provider.CalendarContract
import android.util.Log
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.database.getRegion

/**
 * 系统日历辅助类
 */
class SystemCalendarHelper {

    private val timeZone = "Asia/Shanghai"
    private val region = getRegion()
    private val contentResolver = MyApplication.context.contentResolver

    private val regionName = when (region) {
        2 -> "国服"
        3 -> "台服"
        4 -> "日服"
        else -> ""
    }

    /**
     * 添加日历事件
     *
     */
    fun insert(id: Long, startTime: String, endTime: String, title: String) {
        val calID: Long = id * 10 + region
        val startMillis: Long = fixJpTime(startTime.formatTime, region).toTimestamp
        val endMillis: Long =  fixJpTime(endTime.formatTime, region).toTimestamp

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, endMillis)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, region)
            put(CalendarContract.Events.CALENDAR_ID, calID)
            put(CalendarContract.Events.EVENT_TIMEZONE, timeZone)
        }
        try {
            contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        } catch (e: Exception) {
            Log.e("DEBUG", e.message?:"")
            ToastUtil.short("日历事件添加失败")
        }
    }
}