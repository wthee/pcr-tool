package cn.wthee.pcrtool.utils

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import cn.wthee.pcrtool.databinding.ViewToolbarBinding


class ToolbarUtil(private val toolbar: ViewToolbarBinding) {

    val leftIcon = toolbar.leftIcon
    val rightIcon = toolbar.rightIcon
    val title = toolbar.title

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