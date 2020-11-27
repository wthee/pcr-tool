package cn.wthee.pcrtool.ui.detail.character

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.viewpager.CharacterPagerAdapter
import cn.wthee.pcrtool.adapters.viewpager.DepthPageTransformer
import cn.wthee.pcrtool.databinding.FragmentCharacterPagerBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class CharacterPagerFragment : Fragment() {

    companion object {
        var uid = -1
    }

    private lateinit var binding: FragmentCharacterPagerBinding
    private lateinit var viewPager: ViewPager2
    private val characterAttrViewModel =
        InjectorUtil.providePromotionViewModelFactory().create(CharacterAttrViewModel::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            uid = it.getInt(UID)
        }
        //从 MainPagerFragment CharacterListFragment过渡至次页面
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            duration = resources.getInteger(R.integer.fragment_anim).toLong()
            setAllContainerColors(Color.TRANSPARENT)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterPagerBinding.inflate(inflater, container, false)
        //添加返回fab
        FabHelper.addBackFab()
        init()
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        //共享元素
        prepareTransitions()
        return binding.root
    }

    private fun init() {
        //加载列表
        MainScope().launch {
            val noData = characterAttrViewModel.isUnknow(uid)
            viewPager = binding.root
            viewPager.adapter =
                CharacterPagerAdapter(childFragmentManager, lifecycle, noData)
            viewPager.setPageTransformer(DepthPageTransformer())
            viewPager.offscreenPageLimit = 3
        }

    }

    //配置共享元素动画
    private fun prepareTransitions() {

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                try {
                    if (names!!.isNotEmpty()) {
                        sharedElements ?: return
                        //角色图片
                        val v0 = CharacterBasicInfoFragment.binding.characterPic
                        sharedElements[names[0]] = v0
                    }
                } catch (e: Exception) {
                    Log.e(Constants.LOG_TAG, e.message ?: "")
                }
            }
        })
    }
}
