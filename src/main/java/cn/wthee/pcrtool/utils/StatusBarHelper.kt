package cn.wthee.pcrtool.utils

import android.view.Window
import android.view.WindowManager

object StatusBarHelper {

    fun fullScreen(setFull: Boolean, window: Window) {
        if (setFull) {
            val lp: WindowManager.LayoutParams = window.attributes
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            window.attributes = lp
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            val attr: WindowManager.LayoutParams = window.attributes
            attr.flags = attr.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            window.attributes = attr
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }
}