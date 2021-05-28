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
}

