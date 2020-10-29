package cn.wthee.pcrtool.utils

import android.content.res.Resources
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ViewToolbarBinding


class ToolbarUtil(private val toolbar: ViewToolbarBinding) {

    val leftIcon = toolbar.leftIcon
    val rightIcon = toolbar.rightIcon
    val title = toolbar.title
    val resources: Resources = MyApplication.context.resources

    fun setLeftIcon(resId: Int) {
        leftIcon.visibility = View.VISIBLE
        leftIcon.setImageResource(resId)
    }

    fun setRightIcon(resId: Int) {
        rightIcon.visibility = View.VISIBLE
        rightIcon.setImageResource(resId)
    }


    fun setTitleCenter() {
        val params = title.layoutParams as ConstraintLayout.LayoutParams
        params.startToStart = 0
        params.endToEnd = 0
        params.topToTop = 0
        params.bottomToBottom = 0
        title.layoutParams = params
    }

    fun setTitleColor(resId: Int) {
        title.setTextColor(resources.getColor(resId, null))
    }

    fun setBackground(resId: Int) {
        toolbar.viewToolbar.setBackgroundColor(resources.getColor(resId, null))
    }

    fun setCenterStyle() {
        setTitleCenter()
        setTitleColor(R.color.colorPrimary)
        setLeftIcon(R.drawable.ic_back)
        setBackground(R.color.colorWhite)
    }

    //悬浮窗标题适当缩小
    fun setFloatTitle(){
        setCenterStyle()
        title.textSize = 16f
        val params = toolbar.root.layoutParams as ConstraintLayout.LayoutParams
        params.height = 28.dp
        toolbar.root.layoutParams = params
        val icon = leftIcon.layoutParams
        icon.width = 22.dp
        icon.height = 22.dp
        leftIcon.layoutParams = icon
    }
}