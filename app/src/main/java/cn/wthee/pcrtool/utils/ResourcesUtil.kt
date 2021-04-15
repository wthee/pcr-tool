package cn.wthee.pcrtool.utils

import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import cn.wthee.pcrtool.MyApplication

/**
 * 应用内资源获取
 */
object ResourcesUtil {

    private val resource: Resources = MyApplication.context.resources

    /**
     * 获取颜色
     */
    fun getColor(resId: Int) = ResourcesCompat.getColor(resource, resId, null)

    /**
     * 获取图片
     */
    fun getDrawable(resId: Int) = ResourcesCompat.getDrawable(resource, resId, null)

    /**
     * 获取文本
     */
    fun getString(resId: Int) = MyApplication.context.getString(resId)

}