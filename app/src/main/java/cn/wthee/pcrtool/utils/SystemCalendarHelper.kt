package cn.wthee.pcrtool.utils

import android.content.ContentValues
import android.net.Uri
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

    private val EVENT_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Calendars._ID,                     // 0
        CalendarContract.Calendars.ACCOUNT_NAME,            // 1
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
        CalendarContract.Calendars.OWNER_ACCOUNT            // 3
    )

    private val PROJECTION_ID_INDEX: Int = 0
    private val PROJECTION_ACCOUNT_NAME_INDEX: Int = 1
    private val PROJECTION_DISPLAY_NAME_INDEX: Int = 2
    private val PROJECTION_OWNER_ACCOUNT_INDEX: Int = 3

    //查询日历
    private fun getCalendar() {


    }

    /**
     * 添加日历事件
     *
     */
    fun insert(startTime: String, endTime: String, title: String) {
        val uri: Uri = CalendarContract.Calendars.CONTENT_URI
        val cur = contentResolver.query(uri, EVENT_PROJECTION, null, null, null)
        cur?.use {
            cur.moveToFirst()
            val calID: Long = cur.getLong(PROJECTION_ID_INDEX)

            val startMillis: Long = fixJpTime(startTime.formatTime, region).toTimestamp
            val endMillis: Long = fixJpTime(endTime.formatTime, region).toTimestamp

            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, "$regionName：$title")
                put(CalendarContract.Events.DESCRIPTION, regionName)
                put(CalendarContract.Events.CALENDAR_ID, calID)
                put(CalendarContract.Events.EVENT_TIMEZONE, timeZone)
            }
            try {
                contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
                ToastUtil.short("日程已添加至系统日历~")
            } catch (e: Exception) {
                Log.e("DEBUG", e.message ?: "")
                ToastUtil.short("日程添加失败~")
            }
        }

    }
}