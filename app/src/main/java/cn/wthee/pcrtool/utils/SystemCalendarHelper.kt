package cn.wthee.pcrtool.utils

import android.content.ContentValues
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.MainActivity

/**
 * 添加至系统日历的数据类
 */
data class SystemCalendarEventData(
    val startTime: String,
    val endTime: String,
    var title: String
)

/**
 * 系统日历辅助类
 */
class SystemCalendarHelper {

    private val timeZone = "Asia/Shanghai"
    private val region = MainActivity.regionType
    private val contentResolver = MyApplication.context.contentResolver
    private val addedEvents = arrayListOf<SystemCalendarEventData>()


    private val regionName = getRegionName(region = region)

    private val CALENDAR_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Calendars._ID,
        CalendarContract.Calendars.ACCOUNT_NAME,
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
        CalendarContract.Calendars.OWNER_ACCOUNT
    )

    private val EVENT_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Events.TITLE,
        CalendarContract.Events.DTSTART,
        CalendarContract.Events.DTEND,
        CalendarContract.Events.ORIGINAL_ID,
    )

    private val PROJECTION_ID_INDEX: Int = 0


    /**
     * 添加日历事件
     */
    fun insertEvents(eventList: List<SystemCalendarEventData>) {
        try {
            //新增数量
            var newAddedCount = 0
            //获取日历信息
            val uri: Uri = CalendarContract.Calendars.CONTENT_URI
            val cur = contentResolver.query(uri, CALENDAR_PROJECTION, null, null, null)
            if (cur != null) {
                cur.moveToFirst()
                if (cur.count > 0) {
                    //获取已添加的日程信息
                    val queryLike = getString(R.string.query_calendar_event)
                    val eventUri = CalendarContract.Events.CONTENT_URI
                    val eventCur = contentResolver.query(
                        eventUri,
                        EVENT_PROJECTION,
                        EVENT_PROJECTION[0] + " like '%${queryLike}%'",
                        null,
                        null
                    )
                    eventCur?.use {
                        eventCur.moveToFirst()
                        do {
                            try {
                                val eventTitle = eventCur.getString(0)
                                val eventStartTime = eventCur.getString(1)
                                val eventEndTime = eventCur.getString(2)
                                val eventId = eventCur.getString(3)
                                if (eventEndTime.toLong() < System.currentTimeMillis()) {
                                    contentResolver.delete(
                                        eventUri,
                                        "${EVENT_PROJECTION[3]} =  $eventId",
                                        null
                                    )
                                    continue
                                }
                                //未过期的日程
                                addedEvents.add(
                                    SystemCalendarEventData(
                                        eventStartTime.toLong().simpleDateFormat,
                                        eventEndTime.toLong().simpleDateFormat,
                                        eventTitle
                                    )
                                )

                            } catch (e: Exception) {
                                Log.e("DEBUG", e.toString())
                            }
                        } while (eventCur.moveToNext())
                    }

                    //遍历判断新增日程
                    eventList.forEach {
                        cur.moveToFirst()
                        val title = "$regionName：${it.title}"
                        it.title = title
                        if (!addedEvents.contains(it)) {
                            val calID: Long = cur.getLong(PROJECTION_ID_INDEX)

                            val startMillis: Long =
                                it.startTime.formatTime.fixJpTime.toTimestamp
                            val endMillis: Long =
                                it.endTime.formatTime.fixJpTime.toTimestamp

                            val values = ContentValues().apply {
                                put(CalendarContract.Events.DTSTART, startMillis)
                                put(CalendarContract.Events.DTEND, endMillis)
                                put(CalendarContract.Events.TITLE, title)
                                put(CalendarContract.Events.DESCRIPTION, regionName)
                                put(CalendarContract.Events.CALENDAR_ID, calID)
                                put(CalendarContract.Events.EVENT_TIMEZONE, timeZone)
                            }
                            contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
                            newAddedCount++
                        }
                    }
                    if (newAddedCount > 0) {
                        ToastUtil.short(
                            getString(
                                R.string.calendar_update_success,
                                newAddedCount
                            )
                        )
                    } else {
                        //无更新
                        ToastUtil.short(getString(R.string.calendar_exist))
                    }
                } else {
                    ToastUtil.short(getString(R.string.calendar_not_found))
                }
                cur.close()
            } else {
                ToastUtil.short(getString(R.string.calendar_not_found))
            }
        } catch (e: Exception) {
            ToastUtil.short(getString(R.string.calendar_add_failure))
        }
    }
}