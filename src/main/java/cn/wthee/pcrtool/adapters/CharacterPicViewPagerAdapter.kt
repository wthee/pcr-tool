package cn.wthee.pcrtool.adapters

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wthee.pcrtool.ui.common.ImageFragment

class CharacterPicViewPagerAdapter internal constructor(
    fm: FragmentManager,
    lifeCycle: Lifecycle
) : FragmentStateAdapter(fm, lifeCycle) {

    private val items = mutableListOf<String>()
    val firstElementPosition = Int.MAX_VALUE / 12 * 12

    fun updateList(list: List<String>) {
        items.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int =
        if (items.isNotEmpty()) Int.MAX_VALUE else 0

    override fun createFragment(position: Int) =
        ImageFragment.newInstance(items[position.rem(items.size)])


}