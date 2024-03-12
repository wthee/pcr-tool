package cn.wthee.pcrtool.utils

import android.content.Context
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.ui.MainActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


val df: DateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINESE)
val df1: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE)
val df2: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.CHINESE)

/**
 * 格式化时间 yyyy/MM/dd HH:mm:ss
 */
val String.formatTime: String
    get() {
        //分隔“年月日”和“时分秒”
        val dateList = this.replace("  ", " ")
            .replace("-", "/")
            .split(" ")
        //年月日
        val ymsList = dateList[0].split("/")
        //时分秒默认00
        val hmsList = arrayListOf("00", "00", "00")
        //重新填充时分秒
        if (dateList.size > 1) {
            val newHmsList = dateList[1].split(":")
            if (newHmsList.isNotEmpty()) {
                hmsList[0] = newHmsList[0]
            }
            if (newHmsList.size > 1) {
                hmsList[1] = newHmsList[1]
            }
            if (newHmsList.size > 2) {
                hmsList[2] = newHmsList[2]
            }
        }
        val ymdStr = "${ymsList[0]}/${ymsList[1].fillZero()}/${ymsList[2].fillZero()}"
        val hmsStr = "${hmsList[0].fillZero()}:${hmsList[1].fillZero()}:${hmsList[2].fillZero()}"
        return "$ymdStr $hmsStr"
    }

/**
 * 截取日期年月日
 */
val String.toDate: String
    get() = if (this.isNotBlank()) {
        this.substring(0, kotlin.math.min(10, this.length))
    } else {
        ""
    }

/**
 * 日期格式化 yyyy/MM/dd HH:mm:ss
 */
val String.simpleDateFormat: String
    get() {
        val d = df1.parse(this)!!.time
        return df1.format(Date(d))
    }

/**
 * 毫秒转日期字符串 UTC
 */
val Long.simpleDateFormatUTC: String
    get() {
        val dfUTC = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE)
        dfUTC.timeZone = TimeZone.getTimeZone("UTC")
        return dfUTC.format(Date(this))
    }

/**
 * 小时 - 1
 */
val String.fixJpTime: String
    get() =
        if (this != "") {
            if (MainActivity.regionType == RegionType.JP) {
                try {
                    val d = df1.parse(this)!!.time - 60 * 60 * 1000
                    df1.format(Date(d))
                } catch (e: Exception) {
                    this
                }
            } else {
                this
            }
        } else {
            this
        }

/**
 * 获取当天时间
 */
fun getToday(ms: Boolean = false): String {
    val time = System.currentTimeMillis()
    val date = Date(time)
    return if (ms) {
        df2.format(date)
    } else {
        df1.format(date)
    }
}

/**
 * 获取当前年份
 */
fun getYear(): Int {
    val time = System.currentTimeMillis()
    val date = Date(time)
    val now = Calendar.getInstance()
    now.time = date
    return now[Calendar.YEAR]
}

/**
 *  计算日期字符串间隔天数 yyyy/MM/dd  this - str2 相差天数
 */
fun String.days(str2: String, showDay: Boolean = true): String {
    val day = try {
        val d1 = df.parse(this.formatTime)!!
        val d2 = df.parse(str2.formatTime)!!
        // + 1s
        val time = d1.time - d2.time + 1000
        time / (60 * 60 * 1000 * 24)
    } catch (e: Exception) {
        0
    }
    return if (showDay) {
        getString(R.string.day, day)
    } else {
        day.toString()
    }
}

/**
 *  计算日期字符串间隔天数 yyyy/MM/dd HH:mm:ss this - str2 相差天数、小时
 */
fun String.dates(str2: String): String {
    return try {
        val d1 = df1.parse(this.formatTime)!!
        val d2 = df1.parse(str2.formatTime)!!
        // + 1s
        val time = d1.time - d2.time + 1000
        toTimeText(time)
    } catch (e: Exception) {
        "0"
    }
}

/**
 * 秒数转时分秒字符串
 */
fun toTimeText(time: Long, context: Context = MyApplication.context): String {
    val day = time / (60 * 60 * 1000 * 24)
    val hour = time / (60 * 60 * 1000) - day * 24
    val minute = time % (60 * 60 * 1000) / (60 * 1000)

    val dayText = if (day > 0) {
        getString(context, R.string.day, day)
    } else {
        ""
    }
    val hourText = if (hour > 0) {
        getString(context, R.string.hour, hour)
    } else {
        ""
    }
    val minText = if (minute > 0) {
        getString(context, R.string.minute, minute)
    } else {
        ""
    }

    return "$dayText$hourText$minText"
}

/**
 * 相差的秒数
 */
fun String.second(str2: String): Long {
    return try {
        val d1 = df1.parse(this.formatTime)!!
        val d2 = df1.parse(str2.formatTime)!!
        val time = d1.time - d2.time
        time / 1000
    } catch (e: Exception) {
        0L
    }
}

/**
 * 月份、天份补零
 *
 * @param toLength 最终字符长度
 */
fun String.fillZero(toLength: Int = 2): String {
    var temp = this
    if (this.length < toLength) {
        for (i in 0 until toLength - this.length) {
            temp = "0$temp"
        }
    }
    return temp
}

/**
 * 进行中判断
 */
fun isInProgress(
    today: String,
    startTime: String,
    endTime: String,
    fixJpTime: Boolean = true
): Boolean {
    val sd = if (fixJpTime) startTime.formatTime.fixJpTime else startTime.formatTime
    val ed = if (fixJpTime) endTime.formatTime.fixJpTime else endTime.formatTime
    return today.second(sd) > 0 && ed.second(today) > 0 && ed.second(today) < 31536000
}

/**
 * 预告判断
 */
fun isComingSoon(today: String, startTime: String, fixJpTime: Boolean = true): Boolean {
    val sd = if (fixJpTime) startTime.formatTime.fixJpTime else startTime.formatTime
    return today.second(sd) < 0
}

/**
 * 计算 n 天前的日期
 */
fun calcDate(date: String, n: Int, before: Boolean): String {
    val now = Calendar.getInstance()
    df1.parse(date)?.let {
        now.time = it
    }
    now[Calendar.DATE] = now[Calendar.DATE] + (if (before) -n else n)
    return df1.format(now.time)
}

/**
 * 格式化排行榜日期
 */
val String.fixedLeaderDate: String
    get() = this.substringBefore('日')
        .replace("年", "/")
        .replace("月", "/")
        .formatTime
