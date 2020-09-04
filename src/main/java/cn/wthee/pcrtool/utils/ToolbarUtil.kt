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
    val resources: Resources = MyApplication.getContext().resources

    fun setLeftIcon(resId: Int){
        leftIcon.setImageResource(resId)
    }

    fun setRightIcon(resId: Int){
        rightIcon.setImageResource(resId)
    } 
    
    fun hideLeftIcon(){
        leftIcon.visibility = View.GONE
    }

    fun showLeftIcon(){
        leftIcon.visibility = View.VISIBLE
    }

    fun hideRightIcon(){
        rightIcon.visibility = View.GONE
    }

    fun showRightIcon(){
        rightIcon.visibility = View.VISIBLE
    }

    fun setTitle(text: String){
        title.text = text
    }

    fun setTitleCenter(){
        val params = title.layoutParams as ConstraintLayout.LayoutParams
        params.startToStart = 0
        params.endToEnd = 0
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
}