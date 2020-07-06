package cn.wthee.pcrtool.utils

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.databinding.ViewToolbarBinding


class ToolbarUtil(private val toolbar: ViewToolbarBinding) {

    val leftIcon = toolbar.leftIcon
    val rightIcon = toolbar.rightIcon
    val title = toolbar.title
    val resources = MyApplication.getContext().resources

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

    fun setTitleColor(resId: Int){
        title.setTextColor(resources.getColor(resId, null))
    }

    fun setBackground(resId: Int){
        toolbar.layoutMain.setBackgroundColor(resources.getColor(resId, null))
    }

    fun showPopupMenu(context: Context, menuId: Int, itemClickListener: ItemClickListener) {
        val popupMenu = PopupMenu(context, toolbar.rightIcon)
        popupMenu.menuInflater.inflate(menuId, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { item ->
            itemClickListener.onClick(item)
            true
        }
    }

    interface ItemClickListener{
        fun onClick(item: MenuItem?)
    }
}