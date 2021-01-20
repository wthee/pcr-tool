package cn.wthee.pcrtool.utils

import android.util.DisplayMetrics
import cn.wthee.pcrtool.MyApplication

/**
 * 屏幕参数获取
 */
object ScreenUtil {

    /**
     * 获取 DisplayMetrics
     */
    private fun getDm(): DisplayMetrics {
        val outMetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = ActivityHelper.instance.currentActivity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = ActivityHelper.instance.currentActivity?.windowManager?.defaultDisplay
            @Suppress("DEPRECATION")
            display?.getMetrics(outMetrics)
        }
        return outMetrics
    }

    /**
     * 宽度
     */
    fun getWidth() = getDm().widthPixels

    /**
     * 高度
     */
    fun getHeight() = getDm().heightPixels

}

/**
 *  获取 dp 的像素值
 */
val Int.dp: Int
    get() {
        val scale: Float = MyApplication.context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
