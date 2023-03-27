package cn.wthee.pcrtool.utils

import android.os.Build
import android.util.DisplayMetrics
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.MyApplication
import kotlin.math.max


/**
 * 屏幕参数获取
 */
object ScreenUtil {

    /**
     * 获取 DisplayMetrics
     */
    @Suppress("DEPRECATION")
    private fun getDm(): DisplayMetrics {
        val outMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = ActivityHelper.instance.currentActivity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            val display = ActivityHelper.instance.currentActivity?.windowManager?.defaultDisplay
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
 * 计算 spanCount
 */
val Dp.spanCount: Int
    @Composable
    get() = max(1, LocalView.current.width / this.value.dp2pxNotComposable)

/**
 *  获取 像素 的dp
 */
val Int.px2dp: Int
    get() {
        val scale: Float = MyApplication.context.resources.displayMetrics.density
        return (this / scale + 0.5f).toInt()
    }

/**
 *  获取dp的像素，Composable
 */
val Float.dp2px: Int
    @Composable
    get() {
        val context = LocalContext.current
        val scale: Float = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

/**
 *  获取dp的像素，非 Composable
 */
val Float.dp2pxNotComposable: Int
    get() {
        val scale: Float = MyApplication.context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

