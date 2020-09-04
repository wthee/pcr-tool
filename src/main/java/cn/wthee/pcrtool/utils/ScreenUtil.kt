package cn.wthee.pcrtool.utils

import android.content.Context
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

    fun getHeight(): Int {
        manager?.defaultDisplay?.getMetrics(outMetrics)
        return outMetrics.heightPixels
    }

    fun setAlpha(alpha: Float) {
        val lp: WindowManager.LayoutParams? =
            window?.attributes
        lp?.alpha = alpha
        window?.attributes = lp
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale: Float = context.getResources().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

}