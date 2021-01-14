package cn.wthee.pcrtool.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 动画
 */
object ObjectAnimatorUtil {

    fun enter(onAnimatorListener: OnAnimatorListener, vararg view: View) {
        val holder1 = PropertyValuesHolder.ofFloat("scaleX", 0.95f, 1f)
        val holder2 = PropertyValuesHolder.ofFloat("scaleY", 0.95f, 1f)
        val holder3 = PropertyValuesHolder.ofFloat("translationY", 80f, 0f)
        val holder4 = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)

        view.forEach {
            start(it, onAnimatorListener, holder1, holder2, holder3, holder4)
        }
    }

    private fun start(
        view: View,
        onAnimatorListener: OnAnimatorListener?,
        vararg holders: PropertyValuesHolder
    ) {
        onAnimatorListener?.prev(view)
        MainScope().launch {
            delay(200L)
            ObjectAnimator.ofPropertyValuesHolder(view, *holders).apply {
                duration =
                    MyApplication.context.resources.getInteger(R.integer.fragment_anim).toLong()
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

    interface OnAnimatorListener {
        fun prev(view: View)
        fun start(view: View)
        fun end(view: View)
    }
}