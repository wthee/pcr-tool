package cn.wthee.pcrtool.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.fragment.app.DialogFragment
import com.nineoldandroids.view.ViewHelper


object DrawerUtil {

    private var offsetY = 0
    private var lastY: Int = 0

    /**
     * 传入页面对应的 View [rootView] 和 DialogFragment [df]、不想绑定的view [notBind]
     * 为页面所有布局和控件添加 OnTouchListener
     **/
    @SuppressLint("ClickableViewAccessibility")
    fun bindAllViewOnTouchListener(
        rootView: View,
        df: DialogFragment, notBind: ArrayList<View>?
    ) {
        val views = getAllChildViews(rootView)
        views.add(rootView)
        //不绑定
        notBind?.forEach {
            views.remove(it)
        }

        views.forEach {
            it.setOnTouchListener { _, event ->
                val y = event.rawY.toInt()
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastY = event.rawY.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        offsetY = y - lastY
                        if (offsetY > 0) {
                            ViewHelper.setTranslationY(rootView, offsetY.toFloat())
//                            ViewHelper.setAlpha(rootView, 1 - offsetY.toFloat() / rootView.height)
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (offsetY > 0) {
                            if (offsetY < rootView.height / 4) {
                                //设置动画
                                val anim = TranslateAnimation(0f, 0f, 0f, -offsetY.toFloat())
                                anim.duration = 150
                                anim.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationRepeat(animation: Animation?) {}

                                    override fun onAnimationStart(animation: Animation?) {}

                                    override fun onAnimationEnd(animation: Animation?) {
                                        rootView.clearAnimation()
                                        ViewHelper.setTranslationY(rootView, 0f)
//                                        ViewHelper.setAlpha(rootView, 1f)
                                    }
                                })
                                rootView.startAnimation(anim)
                            } else {
                                df.dismiss()
                            }
                            offsetY = 0
                        }
                    }
                }
                if (it == rootView) {
                    return@setOnTouchListener true
                }
                return@setOnTouchListener false
            }
        }
    }

    private fun getAllChildViews(view: View): MutableList<View> {
        val allchildren = ArrayList<View>()
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val viewchild = view.getChildAt(i)
                allchildren.add(viewchild)
                allchildren.addAll(getAllChildViews(viewchild))
            }
        }
        return allchildren
    }

}