package cn.wthee.pcrtool.adapters

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wthee.pcrtool.ui.tool.news.ToolNewsListFragment

class NewsViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    databaseType: Int
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val mFragments: SparseArray<Fragment> = SparseArray()

    init {
        mFragments.put(0, ToolNewsListFragment.getInstance(if (databaseType == 1) 2 else 4))
        mFragments.put(1, ToolNewsListFragment.getInstance(3))
        mFragments.put(2, ToolNewsListFragment.getInstance(if (databaseType == 1) 4 else 2))

    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getItemCount(): Int {
        return mFragments.size()
    }

}