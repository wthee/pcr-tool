package cn.wthee.pcrtool.adapters.viewpager

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wthee.pcrtool.ui.tool.news.ToolNewsListFragment

class NewsListPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    val mFragments: SparseArray<Fragment> = SparseArray()

    init {
        mFragments.put(0, ToolNewsListFragment.newInstance(2))
        mFragments.put(1, ToolNewsListFragment.newInstance(3))
        mFragments.put(2, ToolNewsListFragment.newInstance(4))
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getItemCount(): Int {
        return mFragments.size()
    }


}