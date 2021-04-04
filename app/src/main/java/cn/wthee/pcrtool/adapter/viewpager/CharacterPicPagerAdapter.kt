package cn.wthee.pcrtool.adapter.viewpager

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wthee.pcrtool.ui.character.pic.CharacterPicFragment

/**
 * 角色图片详情适配器
 * 角色图片链接 [String]
 */
class CharacterPicPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    picData: ArrayList<String>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    val mFragments: SparseArray<Fragment> = SparseArray()

    init {
        picData.forEachIndexed { index, url ->
            mFragments.put(index, CharacterPicFragment.getInstance(index, url))
        }
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getItemCount(): Int {
        return mFragments.size()
    }

}
