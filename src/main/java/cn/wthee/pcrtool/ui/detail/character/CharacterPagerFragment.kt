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
import cn.wthee.pcrtool.adapters.CharacterInfoViewPagerAdapter
import cn.wthee.pcrtool.adapters.ZoomOutPageTransformer
import cn.wthee.pcrtool.data.model.CharacterBasicInfo
import cn.wthee.pcrtool.databinding.FragmentCharacterPagerBinding
import javax.inject.Singleton

@Singleton
class CharacterPagerFragment : Fragment() {

    private lateinit var binding: FragmentCharacterPagerBinding
    private var character: CharacterBasicInfo? = null
    lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            character = it.getSerializable("character") as CharacterBasicInfo?
        }
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterPagerBinding.inflate(inflater, container, false)
        //加载列表
        viewPager = binding.root
        viewPager.adapter =
            CharacterInfoViewPagerAdapter(
                childFragmentManager,
                lifecycle,
                character!!
            )
        viewPager.offscreenPageLimit = 3
        viewPager.setPageTransformer(ZoomOutPageTransformer())
        //???
        if (MainActivity.sp.getBoolean("106001", false)) {
            viewPager.setBackgroundResource(R.drawable.viewpager_bg)
        } else {
            viewPager.background = null
        }
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return binding.root
    }

}
