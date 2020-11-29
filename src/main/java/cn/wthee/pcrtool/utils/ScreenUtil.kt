package cn.wthee.pcrtool.utils

import android.util.DisplayMetrics
import cn.wthee.pcrtool.MyApplication


object ScreenUtil {

    private fun getDm(): DisplayMetrics {
        val outMetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = ActivityUtil.instance.currentActivity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = ActivityUtil.instance.currentActivity?.windowManager?.defaultDisplay
            @Suppress("DEPRECATION")
            display?.getMetrics(outMetrics)
        }
        return outMetrics
    }

    fun getWidth() = getDm().widthPixels

    fun getHeight() = getDm().heightPixels

}

// 获取 dp 的像素值
val Int.dp: Int
    get() {
        val scale: Float = MyApplication.context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
