package cn.wthee.pcrtool.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View

object ObjectAnimatorHelper {

    private val drt = 500L

    fun enter(vararg view: View) {
        val holder1 = PropertyValuesHolder.ofFloat("translationY", 250f, 0f)
        val holder2 = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        view.forEach {
            ObjectAnimator.ofPropertyValuesHolder(it, holder1, holder2).apply {
                duration = 300L
                start()
            }
        }
    }

    fun alpha(vararg view: View) {
        val holder2 = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        view.forEach {
            ObjectAnimator.ofPropertyValuesHolder(it, holder2).apply {
                duration = 800L
                start()
            }
        }
    }

    fun scaleX(vararg view: View) {
        val holder1 = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f)
        view.forEach {
            ObjectAnimator.ofPropertyValuesHolder(it, holder1).apply {
                duration = drt
                start()
            }
        }
    }

    fun scaleY(vararg view: View) {
        val holder2 = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f)
        view.forEach {
            ObjectAnimator.ofPropertyValuesHolder(it, holder2).apply {
                duration = drt
                start()
            }
        }
    }

}