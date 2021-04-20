package cn.wthee.pcrtool.ui.character

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.viewpager.CharacterPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterPagerBinding
import cn.wthee.pcrtool.ui.character.CharacterPagerFragment.Companion.uid
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.DepthPageTransformer
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import coil.load
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
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
@AndroidEntryPoint
class CharacterPagerFragment : Fragment() {

    companion object {
        var uid = -1
    }


    private lateinit var binding: FragmentCharacterPagerBinding
    private val args by navArgs<CharacterPagerFragmentArgs>()

    private val characterAttrViewModel: CharacterAttrViewModel by activityViewModels()
    private val sharedCharacterViewModel: CharacterViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uid = args.unitId

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            duration = 500L
            setAllContainerColors(Color.TRANSPARENT)
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false).apply {
            duration = 500L
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true).apply {
            duration = 900L
        }
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = 500L
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = 500L
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        postponeEnterTransition()
        binding = FragmentCharacterPagerBinding.inflate(inflater, container, false)
        init()
        setListener()
        return binding.root
    }

    private fun setListener() {
        //打开角色图片列表
        binding.characterPic.setOnClickListener {
            val action = CharacterPagerFragmentDirections
                .actionCharacterPagerFragmentToCharacterPicListFragment(uid)
            val extras =
                FragmentNavigatorExtras(
                    binding.characterPic to binding.characterPic.transitionName
                )
            findNavController().navigate(action, extras)
        }
    }

    private fun init() {
        //加载列表
        lifecycleScope.launch {
            //加载图片 toolbar 背景
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
            //加载 viewpager
            binding.characterPager.apply {
                val noData = characterAttrViewModel.isUnknown(uid)
                offscreenPageLimit = 1
                if (adapter == null) {
                    adapter = CharacterPagerAdapter(childFragmentManager, lifecycle, noData, uid)
                    adapter = adapter
                    setPageTransformer(DepthPageTransformer())
                }
            }

        }

    }

}
