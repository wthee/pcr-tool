package cn.wthee.pcrtool.utils

import android.util.DisplayMetrics
import android.view.WindowManager


object ScreenUtil {

    private val manager = ActivityUtil.instance.currentActivity?.windowManager
    private val window = ActivityUtil.instance.currentActivity?.window
    private val outMetrics = DisplayMetrics()

    fun getWidth(): Int {
        manager?.defaultDisplay?.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    fun setAlpha(alpha: Float) {
        val lp: WindowManager.LayoutParams? =
            window?.attributes
        lp?.alpha = alpha
        window?.attributes = lp
    }
}