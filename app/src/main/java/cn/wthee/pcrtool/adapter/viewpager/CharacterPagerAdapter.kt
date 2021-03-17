package cn.wthee.pcrtool.adapter.viewpager

import android.content.Context
import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wthee.pcrtool.ui.character.attr.CharacterAttrFragment
import cn.wthee.pcrtool.ui.character.basic.CharacterBasicInfoFragment
import cn.wthee.pcrtool.ui.skill.SkillFragment

/**
 * 角色详情页面适配器
 * 角色基本信息 [CharacterBasicInfoFragment]
 * 角色属性信息 [CharacterAttrFragment]
 * 角色技能信息 [SkillFragment]
 */
class CharacterPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    noData: Boolean,
    uid: Int
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    val mFragments: SparseArray<Fragment> = SparseArray()

    init {
        mFragments.put(0, CharacterBasicInfoFragment.getInstance(uid))
        if (!noData) {
            mFragments.put(1, CharacterAttrFragment.getInstance(uid))
            mFragments.put(2, SkillFragment.getInstance(uid, 0))
        }
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getItemCount(): Int {
        return mFragments.size()
    }

}


class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginInDp: Int) :
    RecyclerView.ItemDecoration() {

    private val horizontalMarginInPx: Int =
        context.resources.getDimension(horizontalMarginInDp).toInt()

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.right = horizontalMarginInPx
        outRect.left = horizontalMarginInPx
    }

}
