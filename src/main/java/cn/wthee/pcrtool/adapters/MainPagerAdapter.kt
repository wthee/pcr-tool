package cn.wthee.pcrtool.adapters

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.ui.main.EnemyFragment
import cn.wthee.pcrtool.ui.main.EquipmentListFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val mFragments: SparseArray<Fragment> = SparseArray()

    init {
        mFragments.put(PAGE_CHARACTER, CharacterListFragment())
        mFragments.put(PAGE_EQUIP, EquipmentListFragment())
        mFragments.put(PAGE_ENEMY, EnemyFragment())
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getItemCount(): Int {
        return mFragments.size()
    }


    fun setPageTitle(position: Int, title: String) {
        if (position >= 0 && position < mFragments.size()) {

            notifyDataSetChanged();
        }
    }


    companion object {
        const val PAGE_CHARACTER = 0
        const val PAGE_EQUIP = 1
        const val PAGE_ENEMY = 2
    }
}