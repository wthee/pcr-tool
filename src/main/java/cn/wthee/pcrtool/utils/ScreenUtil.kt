package cn.wthee.pcrtool.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.DisplayMetrics
import cn.wthee.pcrtool.MyApplication


object ScreenUtil {

    fun ScreenOrient(activity: Activity): Int {
        var orient = activity.requestedOrientation
        if (orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            val windowManager = activity.windowManager
            val display = windowManager.defaultDisplay
            val screenWidth = display.width
            val screenHeight = display.height
            orient =
                if (screenWidth < screenHeight) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        return orient
    }

    fun getWidth(): Int {
        val dm = DisplayMetrics()
        ActivityUtil.instance.currentActivity?.windowManager?.defaultDisplay?.getMetrics(dm)
        return dm.widthPixels // width
    }

    fun getHeight(): Int {
        val dm = DisplayMetrics()
        ActivityUtil.instance.currentActivity?.windowManager?.defaultDisplay?.getMetrics(dm)
        return dm.heightPixels // width
    }
}

val Int.dp: Float
    get() {
        val scale: Float = MyApplication.getContext().resources.displayMetrics.density
        return (this * scale + 0.5f)
    }

val Int.px: Float
    get() {
        val scale: Float = MyApplication.getContext().resources.displayMetrics.density
        return (this / scale + 0.5f)
    }
