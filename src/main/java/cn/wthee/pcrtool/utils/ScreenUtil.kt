package cn.wthee.pcrtool.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.Display
import cn.wthee.pcrtool.MyApplication


object ScreenUtil {

    fun getWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val display: Display = context.display!!
        display.getRealMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getHeight(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val display: Display = context.display!!
        display.getRealMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}
// 获取 dp 的像素值
val Int.dp: Int
    get() {
        val scale: Float = MyApplication.context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
