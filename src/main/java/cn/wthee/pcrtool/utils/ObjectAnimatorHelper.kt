package cn.wthee.pcrtool.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ObjectAnimatorHelper {

    private const val drt = 500L

    fun enter(vararg view: View) {
        val holder1 = PropertyValuesHolder.ofFloat("translationY", 50f, 0f)
        val holder2 = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        view.forEach {
            start(it, holder1, holder2)
        }
    }

    fun alpha(vararg view: View) {
        val holder = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        view.forEach {
            start(it, holder)
        }
    }

    private fun start(view: View, vararg holders: PropertyValuesHolder) {
        view.visibility = View.INVISIBLE
        MainScope().launch {
            delay(300L)
            ObjectAnimator.ofPropertyValuesHolder(view, *holders).apply {
                duration = 300L
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(animation: Animator?) {
                        view.visibility = View.VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {
                        view.visibility = View.VISIBLE
                    }
                })
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