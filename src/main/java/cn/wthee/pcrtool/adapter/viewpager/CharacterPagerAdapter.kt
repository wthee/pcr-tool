package cn.wthee.pcrtool.adapter.viewpager

import android.util.SparseArray
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.ui.detail.character.attr.CharacterAttrFragment
import cn.wthee.pcrtool.ui.detail.character.basic.CharacterBasicInfoFragment
import cn.wthee.pcrtool.ui.detail.character.skill.CharacterSkillFragment
import kotlin.math.abs

class CharacterPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    noData: Boolean
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val mFragments: SparseArray<Fragment> = SparseArray()

    init {
        mFragments.put(0, CharacterBasicInfoFragment.getInstance())
        if (!noData) {
            mFragments.put(1, CharacterAttrFragment())
            mFragments.put(2, CharacterSkillFragment())
        }
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getItemCount(): Int {
        return mFragments.size()
    }


}

private const val MIN_SCALE = 0.75f

class DepthPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    alpha = 1f
                    translationX = 0f
                    translationZ = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    alpha = 1 - position

                    // Counteract the default slide transition
                    translationX = pageWidth * -position
                    // Move it behind the left page
                    translationZ = -1f

                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position)))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}