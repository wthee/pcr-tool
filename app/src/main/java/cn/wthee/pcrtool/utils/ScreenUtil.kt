package cn.wthee.pcrtool.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
 * @param width 总宽度
 * @param itemDp 子项宽度
 */
fun spanCount(width: Int, itemDp: Dp, context: Context = MyApplication.context) =
    max(1, width / dp2px(itemDp.value, context))

/**
 *  获取 像素 的dp
 */
fun px2dp(context: Context, px: Int): Dp {
    val scale: Float = context.resources.displayMetrics.density
    return (px / scale).dp
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
fun dp2px(dp: Float, context: Context = MyApplication.context): Int {
    val scale: Float = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

