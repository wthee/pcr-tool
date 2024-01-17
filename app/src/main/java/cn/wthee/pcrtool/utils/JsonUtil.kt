package cn.wthee.pcrtool.utils


import kotlinx.serialization.json.Json

/**
 * Json 工具
 */
object JsonUtil {
    /**
     * Json 转化
     */
    inline fun <reified T : Any> fromJson(json: String?): T? {
        return json?.let { Json.decodeFromString<T>(it) }
    }

    /**
     * 转换为数据
     */
    fun toIntList(str: String?) = (fromJson(str) ?: arrayListOf<Double>()).toIntList()
}

/**
 * fromJson 列表默认 double，再转成 int
 */
fun ArrayList<Double>.toIntList():ArrayList<Int>{
    val list = arrayListOf<Int>()
    this.forEach {
        list.add(it.toInt())
    }
    return list
}
