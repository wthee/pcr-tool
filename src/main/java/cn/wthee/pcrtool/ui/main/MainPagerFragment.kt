package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.SharedElementCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.sortAsc
import cn.wthee.pcrtool.MainActivity.Companion.sortType
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.MainPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentMainPagerBinding
import cn.wthee.pcrtool.ui.detail.character.CharacterBasicInfoFragment
import cn.wthee.pcrtool.ui.main.EquipmentListFragment.Companion.asc
import cn.wthee.pcrtool.ui.tool.enemy.EnemyViewModel
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import cn.wthee.pcrtool.utils.Constants.SORT_DATE
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textview.MaterialTextView
import java.lang.Boolean
import java.lang.reflect.Method
import kotlin.collections.set


class MainPagerFragment : Fragment() {

    companion object {
        var cListClick = false
        lateinit var tabLayout: TabLayout
        private var count = 0
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
    private val sharedEnemyViewModel by activityViewModels<EnemyViewModel> {
        InjectorUtil.provideEnemyViewModelFactory()
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
                ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
            else
                ResourcesCompat.getColor(resources, R.color.text, null)
            vh?.setTextColor(color)
            CharacterListFragment.characterList.scrollToPosition(MainActivity.currentCharaPosition)

        } catch (e: java.lang.Exception) {
        }
    }

    // TODO 让菜单同时显示图标和文字
//    override fun onPrepareOptionsMenu(menu: Menu) {
//        super.onPrepareOptionsMenu(menu)
//        if (menu.javaClass == MenuBuilder::class.java) {
//            try {
//                val m: Method =
//                    menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE)
//                m.isAccessible = true
//                m.invoke(menu, true)
//            } catch (e: java.lang.Exception) {
//            }
//        }
//    }


    private fun init() {
        tipText = binding.noDataTip
        //menu
        binding.mainToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_tool_pvp -> findNavController().navigate(R.id.action_containerFragment_to_toolPvpFragment)
                R.id.menu_tool_level -> findNavController().navigate(R.id.action_containerFragment_to_toolLevelFragment)
                R.id.menu_tool_enemy -> findNavController().navigate(R.id.action_containerFragment_to_enemyListFragment)
            }
            FabHelper.addBackFab()
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
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_character, null)
                    tab.text = sp.getInt(Constants.SP_COUNT_CHARACTER, 0).toString()
                    //长按重置
                    tab.view.setOnLongClickListener {
                        CharacterListFragment.characterfilterParams.initData()
                        CharacterListFragment.characterfilterParams.all = true
                        sortType = SORT_DATE
                        sortAsc = false
                        sharedCharacterViewModel.getCharacters(
                            sortType,
                            sortAsc, ""
                        )
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
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_equip, null)
                    tab.text = sp.getInt(Constants.SP_COUNT_EQUIP, 0).toString()
                    //长按重置
                    tab.view.setOnLongClickListener {
                        EquipmentListFragment.equipfilterParams.initData()
                        asc = true
                        sharedEquipViewModel.getEquips(asc, "")
                        return@setOnLongClickListener true
                    }
                    //点击回顶部
                    tab.view.setOnClickListener {
                        if (MainActivity.currentMainPage == position) {
                            EquipmentListFragment.list.smoothScrollToPosition(0)
                        }
                    }
                }
                //怪物
                2 -> {
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_enemy, null)
                    tab.text = sp.getInt(Constants.SP_COUNT_ENEMY, 0).toString()
                    //长按重置
                    tab.view.setOnLongClickListener {
                        sharedEnemyViewModel.getAllEnemy()
                        return@setOnLongClickListener true
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
