package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.databinding.ViewMenuItemBinding

class MenuItemViewHelper(private val binding: ViewMenuItemBinding) {

    fun setItem(title: String, iconId: Int, bgColorId: Int) {
        binding.apply {
            itemTitle.text = title
            itemIcon.setImageDrawable(ResourcesUtil.getDrawable(iconId))
            itemBackground.setBackgroundColor(
                ResourcesUtil.getColor(bgColorId)
            )
        }
    }
}