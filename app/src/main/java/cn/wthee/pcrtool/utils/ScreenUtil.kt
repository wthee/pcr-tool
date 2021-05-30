package cn.wthee.pcrtool.utils

import android.os.Build
import android.util.DisplayMetrics
import android.view.View
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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

    /**
     * 全屏
     */
    fun setFullScreen() {
        val window = ActivityHelper.instance.currentActivity?.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window?.decorView?.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    /**
     * 初始化全屏
     */
    @Suppress("DEPRECATION")
    fun initScreen() {
        val window = ActivityHelper.instance.currentActivity?.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.setDecorFitsSystemWindows(true)
        } else {
            window?.clearFlags(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window?.clearFlags(View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    fun getCharacterCardHeight() = getWidth() / Constants.RATIO
}

/**
 *  获取 像素 的dp
 */
val Int.px2dp: Int
    get() {
        val scale: Float = MyApplication.context.resources.displayMetrics.density
        return (this / scale + 0.5f).toInt()
    }

/**
 *  获取 像素 的dp
 */
val Float.dp2px: Int
    get() {
        val scale: Float = MyApplication.context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
