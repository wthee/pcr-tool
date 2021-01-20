package cn.wthee.pcrtool.utils

import androidx.constraintlayout.widget.ConstraintLayout
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ViewMenuItemBinding

/**
 * 菜单
 */
class MenuItemViewHelper(private val binding: ViewMenuItemBinding) {

    /**
     * 设置菜单项标题 [title]，图标 [iconId]
     */
    fun setItem(title: String, iconId: Int): MenuItemViewHelper {
        if (iconId == R.drawable.ic_calendar) {
            setCenterIcon()
        }
        binding.apply {
            itemTitle.text = title
            itemIcon.setImageDrawable(ResourcesUtil.getDrawable(iconId))
            itemBackground.setBackgroundColor(
                ResourcesUtil.getColor(R.color.colorPrimary)
            )
            root.transitionName = title
        }
        return this
    }

    /**
     * 设置居中样式
     */
    private fun setCenterIcon() {
        binding.itemIcon.apply {
            val params = layoutParams as ConstraintLayout.LayoutParams
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams = params
        }
    }
}