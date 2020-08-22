package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterViewPagerAdapter
import cn.wthee.pcrtool.adapters.DepthPageTransformer
import cn.wthee.pcrtool.data.model.entity.CharacterBasicInfo
import cn.wthee.pcrtool.databinding.FragmentCharacterPagerBinding
import cn.wthee.pcrtool.utils.FabHelper

class CharacterPagerFragment : Fragment() {

    private lateinit var binding: FragmentCharacterPagerBinding
    private var character: CharacterBasicInfo? = null
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            character = it.getSerializable("character") as CharacterBasicInfo?
        }
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        requireActivity().supportPostponeEnterTransition()
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
                character!!
            )
        viewPager.setPageTransformer(DepthPageTransformer())
        viewPager.offscreenPageLimit = 2
        //???
        if (MainActivity.sp.getBoolean("106001", false)) {
            viewPager.setBackgroundResource(R.drawable.viewpager_bg)
        } else {
            viewPager.background = null
        }
    }

}
