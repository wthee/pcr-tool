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
class ToolbarHelper(private val toolbar: ViewToolbarBinding) {

    val leftIcon = toolbar.leftIcon
    val rightIcon = toolbar.rightIcon
    val title = toolbar.title
    val resources: Resources = MyApplication.context.resources

    /**
     * 设置左侧图标
     */
    fun setLeftIcon(resId: Int) {
        leftIcon.visibility = View.VISIBLE
        leftIcon.setImageResource(resId)
    }

    /**
     * 设置主页面样式 Toolbar
     */
    fun setMainToolbar(iconId: Int, titleText: String): ToolbarHelper {
        setLeftIcon(iconId)
        title.text = titleText
        toolbar.root.setBackgroundColor(ResourcesUtil.getColor(R.color.colorPrimary))
        return this
    }

    /**
     * 设置右侧图标
     */
    fun setRightIcon(resId: Int, colorId: Int): ToolbarHelper {
        rightIcon.visibility = View.VISIBLE
        rightIcon.setImageResource(resId)
        rightIcon.imageTintList = ColorStateList.valueOf(ResourcesUtil.getColor(colorId))
        return this
    }

    /**
     * 设置标题居中样式
     */
    private fun setCenterStyle() {
        val params = title.layoutParams as ConstraintLayout.LayoutParams
        params.startToStart = 0
        params.endToEnd = 0
        params.topToTop = 0
        params.bottomToBottom = 0
        title.layoutParams = params
        title.setTextColor(ResourcesUtil.getColor(R.color.colorPrimary))
    }

    /**
     * 设置居中标题
     */
    fun setCenterTitle(titleText: String): ToolbarHelper {
        setCenterStyle()
        title.text = titleText
        return this
    }

}