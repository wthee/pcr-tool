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
import cn.wthee.pcrtool.ui.detail.character.attr.CharacterAttrFragment
import cn.wthee.pcrtool.ui.detail.character.basic.CharacterBasicInfoFragment
import cn.wthee.pcrtool.ui.detail.character.skill.CharacterSkillFragment

class CharacterPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    noData: Boolean,
    uid: Int, r6Id: Int
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val mFragments: SparseArray<Fragment> = SparseArray()

    init {
        mFragments.put(0, CharacterBasicInfoFragment.getInstance(uid, r6Id))
        if (!noData) {
            mFragments.put(1, CharacterAttrFragment.getInstance(uid, r6Id))
            mFragments.put(2, CharacterSkillFragment.getInstance(uid))
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
