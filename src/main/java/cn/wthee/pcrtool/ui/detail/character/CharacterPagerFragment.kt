package cn.wthee.pcrtool.ui.detail.character

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterViewPagerAdapter
import cn.wthee.pcrtool.adapters.DepthPageTransformer
import cn.wthee.pcrtool.databinding.FragmentCharacterPagerBinding
import cn.wthee.pcrtool.utils.FabHelper
import com.google.android.material.transition.MaterialContainerTransform

class CharacterPagerFragment : Fragment() {

    private lateinit var binding: FragmentCharacterPagerBinding
    private var uid = -1
    private lateinit var viewPager: ViewPager2

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
        viewPager = binding.root
        viewPager.adapter =
            CharacterViewPagerAdapter(
                childFragmentManager,
                lifecycle,
                uid
            )
        viewPager.setPageTransformer(DepthPageTransformer())
        viewPager.offscreenPageLimit = 2
    }

}
