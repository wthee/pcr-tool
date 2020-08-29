package cn.wthee.pcrtool.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ObjectAnimatorHelper {

    fun enter(onAnimatorListener: OnAnimatorListener, vararg view: View) {
        val holder1 = PropertyValuesHolder.ofFloat("scaleX", 0.85f, 1f)
        val holder2 = PropertyValuesHolder.ofFloat("scaleY", 0.85f, 1f)
        val holder3 = PropertyValuesHolder.ofFloat("translationY", 150f, 0f)

        view.forEach {
            start(it, onAnimatorListener,  holder1, holder2, holder3)
        }
    }

    fun alpha(vararg view: View) {
        val holder = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        view.forEach {
            start(it, holder)
        }
    }

    private fun start(view: View, onAnimatorListener: OnAnimatorListener?, vararg holders: PropertyValuesHolder) {
        onAnimatorListener?.prev(view)
        MainScope().launch {
            delay(300L)
            ObjectAnimator.ofPropertyValuesHolder(view, *holders).apply {
                duration = 600L
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(animation: Animator?) {
                        onAnimatorListener?.end(view)
                    }

                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {
                        onAnimatorListener?.start(view)
                    }
                })
                start()
            }
        }
    }

    private fun start(view: View, vararg holders: PropertyValuesHolder) {
        view.visibility = View.GONE
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

    interface OnAnimatorListener {
        fun prev(view: View)
        fun start(view: View)
        fun end(view: View)
    }
}