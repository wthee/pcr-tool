package cn.wthee.pcrtool.adapter.viewpager

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wthee.pcrtool.data.view.ClanBattleInfo
import cn.wthee.pcrtool.ui.tool.clan.ClanBossInfoFragment

/**
 * 团队战 BOSS 详情页面适配器
 * 基本信息 [ClanBattleInfo]
 */
class ClanBossPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    section: Int
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    val mFragments: SparseArray<Fragment> = SparseArray()

    init {
        for (i in 0..4) {
            mFragments.put(i, ClanBossInfoFragment.getInstance(i, section))
        }
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getItemCount(): Int {
        return mFragments.size()
    }

}