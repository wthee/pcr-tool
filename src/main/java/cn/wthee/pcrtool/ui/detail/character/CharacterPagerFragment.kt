package cn.wthee.pcrtool.ui.detail.character

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterPagerAdapter
import cn.wthee.pcrtool.adapters.DepthPageTransformer
import cn.wthee.pcrtool.databinding.FragmentCharacterPagerBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CharacterPagerFragment : Fragment() {

    private lateinit var binding: FragmentCharacterPagerBinding
    private var uid = -1
    private lateinit var viewPager: ViewPager2
    private val sharedCharacterAttrViewModel by activityViewModels<CharacterAttrViewModel> {
        InjectorUtil.providePromotionViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            uid = it.getInt("uid")
        }
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            duration = resources.getInteger(R.integer.fragment_anim).toLong()
            setAllContainerColors(Color.TRANSPARENT)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterPagerBinding.inflate(inflater, container, false)
        //添加返回fab
        FabHelper.addBackFab()
        init()
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return binding.root
    }

    private fun init() {
        //加载列表
        var noData = false

        MainScope().launch {
            try {
                sharedCharacterAttrViewModel.isUnknow(uid)
            } catch (e: Exception) {
                noData = true
            }
            viewPager = binding.root
            viewPager.adapter =
                CharacterPagerAdapter(
                    childFragmentManager,
                    lifecycle,
                    uid,
                    noData
                )
            viewPager.setPageTransformer(DepthPageTransformer())
            viewPager.offscreenPageLimit = 2
        }

    }

}
