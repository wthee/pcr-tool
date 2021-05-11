package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.database.getDatabaseType
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


val df: DateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINESE)
val df1: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE)

/**
 * 获取当天时间
 */
fun getToday(): String {
    var time = System.currentTimeMillis()
    if (getDatabaseType() == 2) {
        //日服时区
        time += 60 * 60 * 1000
    }
    val date = Date(time)
    return df1.format(date)
}

/**
 *  把 - 拼接的字符串，转化为数组
 */
fun String.intArrayList(): ArrayList<Int> {
    val list = arrayListOf<Int>()
    val ids = this.split("-")
    ids.forEachIndexed { _, id ->
        if (id != "") {
            list.add(id.toInt())
        }
    }
    return list
}

/**
 * 去除空格等无用字符
 */
fun String.deleteSpace() = this.replace("\\s".toRegex(), "")

/**
 *  计算日期字符串间隔天数 yyyy/MM/dd  this - str2 相差天数
 */
fun String.days(str2: String): String {
    return try {
        val d1 = df.parse(this)!!
        val d2 = df.parse(str2)!!
        "${(d1.time - d2.time) / (60 * 60 * 1000 * 24)}天"
    } catch (e: Exception) {
        "0"
    }
}

/**
 *  计算日期字符串间隔天数 yyyy/MM/dd HH:mm:ss this - str2 相差天数、小时
 */
fun String.dates(str2: String): String {
    return try {
        val d1 = df1.parse(this)!!
        val d2 = df1.parse(str2)!!
        val time = d1.time - d2.time
        val day = time / (60 * 60 * 1000 * 24)
        val hour = time / (60 * 60 * 1000) - day * 24
        if (hour == 0L) {
            "${day}天"
        } else {
            "${day}天${hour}时"
        }
    } catch (e: Exception) {
        "0"
    }
}

/**
 * 相差的小时数
 */
fun String.hourInt(str2: String): Int {
    return try {
        val d1 = df1.parse(this)!!
        val d2 = df1.parse(str2)!!
        ((d1.time - d2.time) / (60 * 60 * 1000.0)).int
    } catch (e: Exception) {
        0
    }
}

/**
 * 月份、天份补零
 */
fun String.fillZero() = if (this.length == 1) "0$this" else this

