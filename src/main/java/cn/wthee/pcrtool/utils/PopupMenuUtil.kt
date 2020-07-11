package cn.wthee.pcrtool.utils

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu

object PopupMenuUtil {
    fun showPopupMenu(
        context: Context,
        menuId: Int,
        anchor: View,
        itemClickListener: ItemClickListener
    ) {
        val popupMenu = PopupMenu(context, anchor)
        popupMenu.menuInflater.inflate(menuId, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { item ->
            itemClickListener.onClick(item)
            true
        }
    }

    interface ItemClickListener {
        fun onClick(item: MenuItem?)
    }
}