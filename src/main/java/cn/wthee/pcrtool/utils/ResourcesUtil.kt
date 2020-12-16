package cn.wthee.pcrtool.utils

import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import cn.wthee.pcrtool.MyApplication

/**
 * 应用内资源获取
 */
object ResourcesUtil {

    private val resource: Resources = MyApplication.context.resources

    fun getColor(resId: Int) = ResourcesCompat.getColor(resource, resId, null)

    fun getDrawable(resId: Int) = ResourcesCompat.getDrawable(resource, resId, null)

}