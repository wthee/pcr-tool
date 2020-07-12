package cn.wthee.pcrtool.utils

object TypeUtil {

    fun listToString(list: List<String>): String {
        var str = ""
        list.forEach {
            str += "$it,"
        }
        return str
    }

    fun stringToList(string: String): List<String> {
        return string.split(",")
    }
}