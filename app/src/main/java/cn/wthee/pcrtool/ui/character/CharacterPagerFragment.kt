package cn.wthee.pcrtool.ui.character

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.viewpager.CharacterPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterPagerBinding
import cn.wthee.pcrtool.ui.character.CharacterPagerFragment.Companion.uid
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.Constants.UNIT_NAME
import cn.wthee.pcrtool.utils.DepthPageTransformer
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ResourcesUtil
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
        lateinit var characterPic: AppCompatImageView
    }


    private lateinit var binding: FragmentCharacterPagerBinding
    private lateinit var adapter: CharacterPagerAdapter
    private var name = ""
    private var nameEx = ""
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
        postponeEnterTransition()
        binding = FragmentCharacterPagerBinding.inflate(inflater, container, false)
        init()
        //角色图片列表
        setListener()
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
        characterPic = binding.characterPic
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
        }

    }

}
