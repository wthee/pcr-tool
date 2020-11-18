package cn.wthee.pcrtool.utils

import android.content.Context
import cn.wthee.pcrtool.MyApplication


object ScreenUtil {

    val display = ActivityUtil.instance.currentActivity?.windowManager?.defaultDisplay

    fun getWidth(context: Context) = display?.width ?: 0

    fun getHeight(context: Context) = display?.height ?: 0

}

// 获取 dp 的像素值
val Int.dp: Int
    get() {
        val scale: Float = MyApplication.context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
