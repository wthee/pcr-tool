package cn.wthee.pcrtool.utils

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.SimpleDateFormat

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

@SuppressLint("SimpleDateFormat")
val df: DateFormat = SimpleDateFormat("yyyy/MM/dd")

/**
 *  计算日期字符串间隔天数 yyyy/MM/dd  this - str2 相差天数
 */
fun String.days(str2: String): String {
    return try {
        val d1 = df.parse(this)!!
        val d2 = df.parse(str2)!!
        String.format("%02d", (d1.time - d2.time) / (60 * 60 * 1000 * 24))
    } catch (e: Exception) {
        "00"
    }
}

fun String.daysInt(str2: String): Int {
    return try {
        val d1 = df.parse(this)!!
        val d2 = df.parse(str2)!!
        ((d1.time - d2.time) / (60 * 60 * 1000 * 24)).toInt()
    } catch (e: Exception) {
        0
    }
}

/**
 * 月份、天份补零
 */
fun String.fillZero() = if (this.length == 1) "0$this" else this

