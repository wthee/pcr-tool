package cn.wthee.pcrtool.adapter.viewpager

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wthee.pcrtool.ui.common.ImageFragment

class CharacterPicPagerAdapter internal constructor(
    fm: FragmentManager,
    lifeCycle: Lifecycle
) : FragmentStateAdapter(fm, lifeCycle) {

    private val items = mutableListOf<String>()

    fun updateList(list: List<String>) {
        items.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int) =
        ImageFragment.newInstance(position, items[position])

}
