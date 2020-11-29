package cn.wthee.pcrtool.utils

import android.content.res.Resources
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ViewToolbarBinding


class ToolbarUtil(toolbar: ViewToolbarBinding) {

    val leftIcon = toolbar.leftIcon
    val rightIcon = toolbar.rightIcon
    val title = toolbar.title
    val resources: Resources = MyApplication.context.resources

    fun setLeftIcon(resId: Int) {
        leftIcon.visibility = View.VISIBLE
        leftIcon.setImageResource(resId)
    }

    fun setRightIcon(resId: Int): ToolbarUtil {
        rightIcon.visibility = View.VISIBLE
        rightIcon.setImageResource(resId)
        return this
    }


    private fun setTitleColor(resId: Int) {
        title.setTextColor(resources.getColor(resId, null))
    }


    private fun setCenterStyle() {
        val params = title.layoutParams as ConstraintLayout.LayoutParams
        params.startToStart = 0
        params.endToEnd = 0
        params.topToTop = 0
        params.bottomToBottom = 0
        title.layoutParams = params
        setTitleColor(R.color.colorPrimary)
        setLeftIcon(R.drawable.ic_back)
    }

    fun setCenterTitle(titleText: String): ToolbarUtil {
        setCenterStyle()
        title.text = titleText
        return this
    }

}