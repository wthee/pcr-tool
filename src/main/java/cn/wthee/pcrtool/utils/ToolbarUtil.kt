package cn.wthee.pcrtool.utils

import android.content.res.ColorStateList
import android.content.res.Resources
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ViewToolbarBinding

/**
 * toolbar 设置
 */
class ToolbarUtil(private val toolbar: ViewToolbarBinding) {

    val leftIcon = toolbar.leftIcon
    val rightIcon = toolbar.rightIcon
    val title = toolbar.title
    val resources: Resources = MyApplication.context.resources

    fun setLeftIcon(resId: Int) {
        leftIcon.visibility = View.VISIBLE
        leftIcon.setImageResource(resId)
    }

    //主页面toolbar
    fun setMainToolbar(iconId: Int, titleText: String): ToolbarUtil {
        setLeftIcon(iconId)
        title.text = titleText
        toolbar.root.setBackgroundColor(ResourcesUtil.getColor(R.color.colorPrimary))
        return this
    }

    fun setRightIcon(resId: Int, colorId: Int): ToolbarUtil {
        rightIcon.visibility = View.VISIBLE
        rightIcon.setImageResource(resId)
        rightIcon.imageTintList = ColorStateList.valueOf(ResourcesUtil.getColor(colorId))
        return this
    }

    private fun setCenterStyle() {
        val params = title.layoutParams as ConstraintLayout.LayoutParams
        params.startToStart = 0
        params.endToEnd = 0
        params.topToTop = 0
        params.bottomToBottom = 0
        title.layoutParams = params
        title.setTextColor(ResourcesUtil.getColor(R.color.colorPrimary))
    }

    fun setCenterTitle(titleText: String): ToolbarUtil {
        setCenterStyle()
        title.text = titleText
        return this
    }

}