package cn.wthee.pcrtool.ui.character

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.viewpager.CharacterPagerAdapter
import cn.wthee.pcrtool.adapter.viewpager.HorizontalMarginItemDecoration
import cn.wthee.pcrtool.databinding.FragmentCharacterPagerBinding
import cn.wthee.pcrtool.ui.character.attr.CharacterAttrViewModel
import cn.wthee.pcrtool.ui.character.attr.CharacterRankCompareFragment
import cn.wthee.pcrtool.ui.character.basic.CharacterBasicInfoFragment
import cn.wthee.pcrtool.ui.character.skill.CharacterSkillLoopDialogFragment
import cn.wthee.pcrtool.utils.Constants.R6ID
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ResourcesUtil
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 角色信息 ViewPager
 */
class CharacterPagerFragment : Fragment() {

    companion object {
        lateinit var viewPager: ViewPager2
        var uid = -1
        var r6Id = -1
    }


    private lateinit var binding: FragmentCharacterPagerBinding
    private lateinit var adapter: CharacterPagerAdapter
    private val characterAttrViewModel by activityViewModels<CharacterAttrViewModel> {
        InjectorUtil.provideCharacterAttrViewModelFactory()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            uid = it.getInt(UID)
            r6Id = it.getInt(R6ID)
        }
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = resources.getColor(R.color.viewpager_bg, null)
            duration = 500L
            setAllContainerColors(Color.TRANSPARENT)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterPagerBinding.inflate(inflater, container, false)
        init()
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.characterPager.adapter = null
    }

    private fun init() {
        //加载列表
        MainScope().launch {
            val noData = characterAttrViewModel.isUnknown(uid)
            viewPager = binding.characterPager
            if (viewPager.adapter == null) {
                adapter = CharacterPagerAdapter(childFragmentManager, lifecycle, noData, uid, r6Id)
                viewPager.adapter = adapter
                viewPager.adjustViewPager(requireContext())
            }
            viewPager.offscreenPageLimit = 3
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    fabChange(position)
                }
            })
        }

    }


    fun fabChange(position: Int) {
        when (position) {
            0 -> {
                binding.fabCharacter.apply {
                    text = getString(R.string.view_pic)
                    icon = ResourcesUtil.getDrawable(R.drawable.ic_pic)
                    setOnClickListener {
                        CharacterBasicInfoFragment.characterPic.callOnClick()
                    }
                }
            }
            1 -> {
                binding.fabCharacter.apply {
                    text = getString(R.string.rank_compare)
                    icon = ResourcesUtil.getDrawable(R.drawable.ic_compare)
                    setOnClickListener {
                        CharacterRankCompareFragment().show(parentFragmentManager, "rank_compare")
                    }
                }
            }
            2 -> {
                binding.fabCharacter.apply {
                    text = getString(R.string.skill_loop)
                    icon = ResourcesUtil.getDrawable(R.drawable.ic_loop)
                    setOnClickListener {
                        CharacterSkillLoopDialogFragment.getInstance(uid)
                            .show(parentFragmentManager, "loop")
                    }
                }
            }
        }
    }

    private fun ViewPager2.adjustViewPager(context: Context) {
        val nextItemVisiblePx = context.resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            context.resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.1f * kotlin.math.abs(position))
            // If you want a fading effect uncomment the next line:
            // page.alpha = 0.25f + (1 - abs(position))
        }
        this.setPageTransformer(pageTransformer)
        val itemDecoration = HorizontalMarginItemDecoration(
            context,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        this.addItemDecoration(itemDecoration)
    }


}
