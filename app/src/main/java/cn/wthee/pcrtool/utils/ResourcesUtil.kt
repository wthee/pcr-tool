package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.MyApplication

/**
 * 应用内资源获取
 */
object ResourcesUtil {

    /**
     * 获取文本
     */
    fun getString(resId: Int) = MyApplication.context.getString(resId)

}