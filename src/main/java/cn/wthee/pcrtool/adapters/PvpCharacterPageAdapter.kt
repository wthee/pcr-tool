package cn.wthee.pcrtool.adapters

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wthee.pcrtool.ui.tool.ToolPvpCharacterIconFragment

class PvpCharacterPageAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val mFragments: SparseArray<Fragment> = SparseArray()

    init {
        mFragments.put(0, ToolPvpCharacterIconFragment.newInstance(1))
        mFragments.put(1, ToolPvpCharacterIconFragment.newInstance(2))
        mFragments.put(2, ToolPvpCharacterIconFragment.newInstance(3))
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getItemCount(): Int {
        return mFragments.size()
    }

}