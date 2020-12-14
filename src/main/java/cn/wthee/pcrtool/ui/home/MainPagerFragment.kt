package cn.wthee.pcrtool.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.viewpager.MainPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentMainPagerBinding
import cn.wthee.pcrtool.ui.detail.character.basic.CharacterBasicInfoFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textview.MaterialTextView
import kotlin.collections.set

/**
 * 主页面 ViewPager
 */
class MainPagerFragment : Fragment() {

    companion object {
        var cListClick = false
        lateinit var tabLayout: TabLayout
        lateinit var tipText: MaterialTextView
    }

    private lateinit var binding: FragmentMainPagerBinding
    private lateinit var viewPager2: ViewPager2
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }
    private val sharedEquipViewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainPagerBinding.inflate(inflater, container, false)
        init()
        prepareTransitions()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            MainActivity.pageLevel = 0
            //刷新收藏
            val vh = CharacterListFragment.characterList.findViewHolderForAdapterPosition(
                MainActivity.currentCharaPosition
            )?.itemView?.findViewById<MaterialTextView>(R.id.name)
            val color = if (CharacterBasicInfoFragment.isLoved)
                ResourcesUtil.getColor(R.color.colorPrimary)
            else
                ResourcesUtil.getColor(R.color.text)
            vh?.setTextColor(color)
            CharacterListFragment.characterList.scrollToPosition(MainActivity.currentCharaPosition)

        } catch (e: java.lang.Exception) {
        }
    }

    private fun init() {
        tipText = binding.noDataTip
        //禁止连续点击
        cListClick = false
        //viewpager2 配置
        viewPager2 = binding.mainViewPager
        viewPager2.offscreenPageLimit = 2
        viewPager2.adapter = MainPagerAdapter(requireActivity())
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                MainActivity.currentMainPage = position
                when (position) {
                    0 -> tipText.text = getString(R.string.data_null_character)
                    1 -> tipText.text = getString(R.string.data_null_equip)
                }
            }
        })
        //toolbar
        ToolbarUtil(binding.mainToolbar)
            .setMainToolbar(getString(R.string.app_name))
        //tab 初始化
        tabLayout = binding.layoutTab
        //绑定tablayout
        TabLayoutMediator(
            tabLayout,
            viewPager2
        ) { tab, position ->
            when (position) {
                //角色
                0 -> {
                    tab.icon =
                        ResourcesUtil.getDrawable(R.drawable.ic_character)
                    tab.text = sp.getInt(Constants.SP_COUNT_CHARACTER, 0).toString()
                    //长按重置
                    tab.view.setOnLongClickListener {
                        sharedCharacterViewModel.reset.postValue(true)
                        return@setOnLongClickListener true
                    }
                    //点击回顶部
                    tab.view.setOnClickListener {
                        if (MainActivity.currentMainPage == position) {
                            CharacterListFragment.characterList.smoothScrollToPosition(0)
                        }
                    }
                }
                //装备
                1 -> {
                    tab.icon = ResourcesUtil.getDrawable(R.drawable.ic_equip)
                    tab.text = sp.getInt(Constants.SP_COUNT_EQUIP, 0).toString()
                    //长按重置
                    tab.view.setOnLongClickListener {
                        sharedEquipViewModel.reset.postValue(true)
                        return@setOnLongClickListener true
                    }
                    //点击回顶部
                    tab.view.setOnClickListener {
                        if (MainActivity.currentMainPage == position) {
                            EquipmentListFragment.list.smoothScrollToPosition(0)
                        }
                    }
                }
            }
        }.attach()

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
                        //角色列表
                        val vh =
                            CharacterListFragment.characterList.findViewHolderForAdapterPosition(
                                MainActivity.currentCharaPosition
                            ) ?: return
                        val v0 = vh.itemView
                        sharedElements[names[0]] = v0
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, e.message ?: "")
                }
            }
        })
    }
}