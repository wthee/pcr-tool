package cn.wthee.pcrtool.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View

object OnTouchUtil {

    private val holder1 = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.9f)
    private val holder2 = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.9f)
    private val holder3 = PropertyValuesHolder.ofFloat("scaleX", 0.9f, 1f)
    private val holder4 = PropertyValuesHolder.ofFloat("scaleY", 0.9f, 1f)
    private val durationTime = 600L


    fun addEffect(view: View) {
        ObjectAnimator.ofPropertyValuesHolder(view, holder1, holder2, holder3, holder4).apply {
            duration = durationTime
            start()
        }
    }
}