package cn.wthee.pcrtool.utils

import android.animation.ObjectAnimator
import android.view.View

object ObjectAnimatorHelper {

    private val drt = 1500L

    fun alpha(vararg view: View) {
        view.forEach {
            ObjectAnimator.ofFloat(it, "alpha", 0f, 1f).apply {
                duration = drt
                start()
            }
        }
    }
}