package cn.wthee.pcrtool.adapters

import android.util.SparseArray
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.data.model.CharacterBasicInfo
import cn.wthee.pcrtool.ui.detail.character.CharacterBasicInfoFragment
import cn.wthee.pcrtool.ui.detail.character.CharacterSkillFragment
import cn.wthee.pcrtool.ui.detail.character.PromotionFragment
import kotlin.math.abs
import kotlin.math.max

class CharacterInfoViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    characterInfo: CharacterBasicInfo
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val mFragments: SparseArray<Fragment> = SparseArray()

    init {
        mFragments.put(PAGE_BASIC, CharacterBasicInfoFragment.getInstance(characterInfo))
        mFragments.put(
            PAGE_BATTLE,
            PromotionFragment.getInstance(
                characterInfo.id,
                characterInfo.name,
                characterInfo.getFixedComment()
            )
        )
        mFragments.put(PAGE_SKILL, CharacterSkillFragment.getInstance(characterInfo.id))
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getItemCount(): Int {
        return mFragments.size()
    }

    companion object {

        const val PAGE_BASIC = 0
        const val PAGE_BATTLE = 1
        const val PAGE_SKILL = 2

    }
}

private const val MIN_SCALE = 0.95f
private const val MIN_ALPHA = 1f

class ZoomOutPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            val pageHeight = height
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 1 -> { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    val scaleFactor = max(MIN_SCALE, 1 - abs(position))
                    val vertMargin = pageHeight * (1 - scaleFactor) / 2
                    val horzMargin = pageWidth * (1 - scaleFactor) / 2
                    translationX = if (position < 0) {
                        horzMargin - vertMargin / 2
                    } else {
                        horzMargin + vertMargin / 2
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Fade the page relative to its size.
                    alpha = (MIN_ALPHA +
                            (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}
