package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.databinding.ViewMenuItemBinding

class MenuItemViewHelper(private val binding: ViewMenuItemBinding) {

    fun setMenuItem(textId: Int, iconId: Int, backgroundColorId: Int) {
        setTitleText(textId)
        setItemBackground(backgroundColorId)
        setIcon(iconId)
    }

    fun setTitleText(resId: Int) {
        binding.itemTitle.text = MyApplication.context.getString(resId)
    }

    fun setItemBackground(resId: Int) {
        binding.itemBg.setBackgroundResource(resId)
    }

    fun setIcon(resId: Int) {
        binding.itemIcon.background = ResourcesUtil.getDrawable(resId)
    }
}