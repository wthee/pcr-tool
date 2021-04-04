package cn.wthee.pcrtool.ui.character

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.viewpager.CharacterPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterPagerBinding
import cn.wthee.pcrtool.ui.character.CharacterPagerFragment.Companion.uid
import cn.wthee.pcrtool.ui.character.attr.CharacterAttrFragment
import cn.wthee.pcrtool.ui.character.attr.CharacterDropDialogFragment
import cn.wthee.pcrtool.ui.home.CharacterListFragment
import cn.wthee.pcrtool.ui.skill.SkillFragment
import cn.wthee.pcrtool.ui.skill.SkillLoopDialogFragment
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.Constants.UNIT_NAME
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import coil.load
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.launch

/**
 * 角色详情 ViewPager
 *
 * 根据 [uid] 显示角色数据
 *
 * 页面布局 [FragmentCharacterPagerBinding]
 *
 * ViewModels [CharacterViewModel] [CharacterAttrViewModel]
 */
class CharacterPagerFragment : Fragment() {

    companion object {
        var uid = -1
        var currentPage = 0
    }


    private lateinit var binding: FragmentCharacterPagerBinding
    private lateinit var adapter: CharacterPagerAdapter
    private var name = ""
    private var nameEx = ""
    private var isLoved = false
    private var pageIndex = 0
    private lateinit var viewPager: ViewPager2

    private val characterAttrViewModel by activityViewModels<CharacterAttrViewModel> {
        InjectorUtil.provideCharacterAttrViewModelFactory()
    }
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            uid = it.getInt(UID)
            name = it.getString(UNIT_NAME) ?: ""
            nameEx = it.getString(Constants.UNIT_NAME_EX) ?: ""
        }
        isLoved = CharacterListFragment.characterFilterParams.starIds.contains(
            CharacterPagerFragment.uid
        )
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            duration = 500L
            setAllContainerColors(ResourcesUtil.getColor(R.color.colorWhite))
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterPagerBinding.inflate(inflater, container, false)
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        init()
        //角色图片列表
        setListener()
        //初始收藏
        setLove(isLoved)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::binding.isInitialized) {
            binding.characterPager.adapter = null
        }
    }

    private fun setListener() {
        binding.characterPic.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(UID, uid)
            val extras =
                FragmentNavigatorExtras(
                    binding.characterPic to binding.characterPic.transitionName
                )
            findNavController().navigate(
                R.id.action_characterPagerFragment_to_characterPicListFragment,
                bundle,
                null,
                extras
            )
        }
    }

    private fun init() {
        //加载列表
        lifecycleScope.launch {
            //toolbar 背景
            val picUrl =
                Constants.CHARACTER_FULL_URL + (uid + if (sharedCharacterViewModel.getR6Ids()
                        .contains(uid)
                ) 60 else 30) + Constants.WEBP
            binding.characterPic.transitionName = picUrl
            binding.root.transitionName = "item_$uid"
            //加载图片
            binding.characterPic.load(picUrl) {
                error(R.drawable.error)
                placeholder(R.drawable.load)
                listener(onStart = {
                    startPostponedEnterTransition()
                })
            }
            val noData = characterAttrViewModel.isUnknown(uid)
            //加载 viewpager
            viewPager = binding.characterPager
            viewPager.offscreenPageLimit = 1
            if (viewPager.adapter == null) {
                adapter = CharacterPagerAdapter(childFragmentManager, lifecycle, noData, uid)
                viewPager.adapter = adapter
                viewPager.setPageTransformer(DepthPageTransformer())
            }
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    pageIndex = position
                    fabChange()
                    currentPage = position
                }
            })
        }

    }


    /**
     * 底部 fab 动态切换
     */
    private fun fabChange() {
        when (pageIndex) {
            0 -> {
                binding.fabCharacter.apply {
                    text = getString(R.string.view_pic)
                    icon = ResourcesUtil.getDrawable(R.drawable.ic_pic)
                    setOnClickListener {
                        binding.characterPic.callOnClick()
                    }
                }
                binding.fabShare.apply {
                    setImageResource(R.drawable.ic_loved)
                    setLove(isLoved)
                    setOnClickListener {
                        isLoved = !isLoved
                        CharacterListFragment.characterFilterParams.addOrRemove(
                            CharacterPagerFragment.uid
                        )
                        setLove(isLoved)
                    }
                }
            }
            1 -> {
                binding.fabCharacter.apply {
                    text = getString(R.string.rank_equip_statistics)
                    icon = ResourcesUtil.getDrawable(R.drawable.ic_compare)
                    setOnClickListener {
                        val args = Bundle().apply {
                            putInt(UID, uid)
                        }
                        findNavController().navigate(
                            R.id.action_characterPagerFragment_to_characterRankRangeEquipFragment,
                            args,
                            null,
                            null
                        )
                    }
                }
                binding.fabShare.apply {
                    //掉落信息时
                    setImageResource(R.drawable.ic_drop)
                    setOnClickListener {
                        CharacterDropDialogFragment.getInstance(CharacterAttrFragment.uid)
                            .show(parentFragmentManager, "character_drop")
                    }
                }
            }
            2 -> {
                binding.fabCharacter.apply {
                    text = getString(R.string.skill_loop)
                    icon = ResourcesUtil.getDrawable(R.drawable.ic_loop)
                    setOnClickListener {
                        SkillLoopDialogFragment.getInstance(uid)
                            .show(parentFragmentManager, "loop")
                    }
                }
                binding.fabShare.apply {
                    setImageResource(R.drawable.ic_share)
                    setOnClickListener {
                        ShareIntentUtil.imageLong(
                            requireActivity(),
                            SkillFragment.shareSkillList,
                            "skill_${uid}.png"
                        )
                    }
                }
            }
        }
    }

    /**
     * 更新收藏按钮颜色
     */
    private fun setLove(isLoved: Boolean) {
        binding.fabShare.setImageResource(if (isLoved) R.drawable.ic_loved else R.drawable.ic_loved_line)
    }
}
