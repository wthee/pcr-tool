package cn.wthee.pcrtool.utils


import com.google.gson.Gson

/**
 * Gson 工具
 */
object GsonUtil {
    /**
     * Gson 转化
     */
    inline fun <reified T : Any> fromJson(json: String?): T? {
        return Gson().fromJson(json, T::class.java)
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
