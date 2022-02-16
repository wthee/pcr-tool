package cn.wthee.pcrtool.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


val df: DateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINESE)
val df1: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE)

/**
 * 格式化时间 yyyy/MM/dd HH:mm:ss
 */
val String.formatTime: String
    get() {
        var list = this.split(" ")[0].split("/")
        if (list.size == 1) {
            list = this.split(" ")[0].split("-")
        }
        return "${list[0]}/${list[1].fillZero()}/${list[2].fillZero()}" + if (this.length > 12) {
            var hms = this.substring(this.length - 8, this.length)
            hms = hms.replace(' ', '0')
            " $hms"
        } else {
            ""
        }
    }

/**
 * 小时 - 1
 */
fun fixJpTime(date: String, type: Int): String = if (date != "") {
    if (type == 4) {
        try {
            val d = df1.parse(date)!!.time - 60 * 60 * 1000
            df1.format(Date(d))
        } catch (e: Exception) {
            date
        }
    } else {
        date
    }
} else {
    ""
}

/**
 * 获取当天时间
 */
fun getToday(): String {
    val time = System.currentTimeMillis()
    val date = Date(time)
    return df1.format(date)
}

/**
 *  计算日期字符串间隔天数 yyyy/MM/dd  this - str2 相差天数
 */
fun String.days(str2: String): String {
    return try {
        val d1 = df.parse(this.formatTime)!!
        val d2 = df.parse(str2.formatTime)!!
        // + 1s
        val time = d1.time - d2.time + 1000
        "${time / (60 * 60 * 1000 * 24)}天"
    } catch (e: Exception) {
        "0天"
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
        val day = time / (60 * 60 * 1000 * 24)
        val hour = time / (60 * 60 * 1000) - day * 24
        val min = time % (60 * 60 * 1000) / (60 * 1000)
        if (day == 0L) {
            if (hour == 0L) {
                "${min}分"
            } else {
                "${hour}时${min}分"
            }
        } else {
            "${day}天${hour}时${min}分"

        }
    } catch (e: Exception) {
        "0"
    }
}

/**
 * 相差的秒数
 */
fun String.second(str2: String): Long {
    return try {
        val d1 = df1.parse(this.formatTime)!!
        val d2 = df1.parse(str2.formatTime)!!
        // + 1s
        val time = d1.time - d2.time + 1000
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
fun isInProgress(today: String, startTime: String, endTime: String, regionType: Int): Boolean {
    val sd = fixJpTime(startTime.formatTime, regionType)
    val ed = fixJpTime(endTime.formatTime, regionType)
    return today.second(sd) > 0 && ed.second(today) > 0 && ed.second(today) < 31536000
}

/**
 * 进行中判断
 */
fun isComingSoon(today: String, startTime: String, regionType: Int): Boolean {
    val sd = fixJpTime(startTime.formatTime, regionType)
    return today.second(sd) < 0
}