package cn.wthee.pcrtool

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.adapters.viewpager.MainPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentMainPagerBinding
import cn.wthee.pcrtool.databinding.LayoutPopupMenuBinding
import cn.wthee.pcrtool.ui.detail.character.CharacterBasicInfoFragment
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.ui.main.EquipmentListFragment
import cn.wthee.pcrtool.ui.main.EquipmentViewModel
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ResourcesUtil
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textview.MaterialTextView
import kotlin.collections.set


class MainPagerFragment : Fragment() {

    companion object {
        var cListClick = false
        lateinit var tabLayout: TabLayout
        lateinit var tipText: MaterialTextView
    }

    private lateinit var binding: FragmentMainPagerBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var popupMenu: PopupWindow
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }
    private val sharedEquipViewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainPagerBinding.inflate(inflater, container, false)
        init()
        prepareTransitions()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            MainActivity.isHome = true
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
        //menu
        binding.mainToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_tool_news -> {
                    findNavController().navigate(R.id.action_containerFragment_to_toolNewsFragment)
                }
                R.id.menu_tool_pvp -> {
                    findNavController().navigate(R.id.action_containerFragment_to_toolPvpFragment)
                }
                R.id.show_more -> {
                    //显示
                    showPopupMenu().apply {
                        root.setOnClickListener {
                            popupMenu.dismiss()
                        }
                        toolLevel.setOnClickListener {
                            popupMenu.dismiss()
                            findNavController().navigate(R.id.action_containerFragment_to_toolLevelFragment)
                        }
                        toolEnemy.setOnClickListener {
                            popupMenu.dismiss()
                            findNavController().navigate(R.id.action_containerFragment_to_enemyListFragment)
                        }
                        toolGacha.setOnClickListener {
                            popupMenu.dismiss()
                            findNavController().navigate(R.id.action_containerFragment_to_toolGachaFragment)
                        }
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }
        //禁止连续点击
        cListClick = false
        //viewpager2 配置
        viewPager2 = binding.viewPager
        viewPager2.offscreenPageLimit = 2
        viewPager2.adapter = MainPagerAdapter(requireActivity())
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                MainActivity.currentMainPage = position
                when (position) {
                    0 -> tipText.text = getString(R.string.data_null_character)
                    1 -> tipText.text = getString(R.string.data_null_equip)
                    2 -> tipText.text = getString(R.string.data_null_enemy)
                }
            }
        })
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

    private fun showPopupMenu(): LayoutPopupMenuBinding {

        val menuBinding = LayoutPopupMenuBinding.inflate(layoutInflater)

        popupMenu = PopupWindow(context)
        popupMenu.contentView = menuBinding.root
        popupMenu.width = LinearLayout.LayoutParams.MATCH_PARENT
        popupMenu.height = LinearLayout.LayoutParams.MATCH_PARENT
        popupMenu.isFocusable = true
        popupMenu.setBackgroundDrawable(ResourcesUtil.getDrawable(R.color.colorAlphtBlack))
        popupMenu.showAtLocation(menuBinding.root, Gravity.NO_GRAVITY, 0, 0)

        return menuBinding
    }

    //配置共享元素动画
    private fun prepareTransitions() {

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                //返回时隐藏toolbar
                binding.layoutToolbar.setExpanded(false)
                try {
                    if (names!!.isNotEmpty()) {
                        sharedElements ?: return
                        //角色列表
                        val vh =
                            CharacterListFragment.characterList.findViewHolderForAdapterPosition(
                                MainActivity.currentCharaPosition
                            ) ?: return
                        val v0 =
                            vh.itemView.findViewById<MaterialCardView>(R.id.item_character)
                        sharedElements[names[0]] = v0
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, e.message ?: "")
                }
            }
        })
    }
}
