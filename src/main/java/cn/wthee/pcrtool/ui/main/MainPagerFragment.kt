package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.SharedElementCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.MainPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentMainPagerBinding
import cn.wthee.pcrtool.ui.detail.character.CharacterBasicInfoFragment
import cn.wthee.pcrtool.ui.main.EquipmentListFragment.Companion.isList
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.PopupMenuUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.collections.set


class MainPagerFragment : Fragment() {

    companion object {
        var cListClick = false
        lateinit var tabLayout: TabLayout
        lateinit var progress: ProgressBar
    }

    private lateinit var binding: FragmentMainPagerBinding
    private lateinit var viewPager2: ViewPager2

    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainPagerBinding.inflate(inflater, container, false)
        init()
        setListener()
        prepareTransitions()
        //设置toolbar
        setHasOptionsMenu(true)
        val toolbar = ToolbarUtil(binding.toolbar)
        toolbar.setLeftIcon(R.drawable.ic_logo)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            //刷新收藏
            val vh = CharacterListFragment.characterList.findViewHolderForAdapterPosition(
                MainActivity.currentCharaPosition
            )?.itemView?.findViewById<AppCompatImageView>(R.id.love)
            vh?.visibility =
                if (CharacterBasicInfoFragment.isLoved) View.VISIBLE else View.INVISIBLE
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        binding.progress.visibility = View.GONE
    }

    private fun init() {
        //禁止连续点击
        cListClick = false
        progress = binding.progress
        //viewpager2 配置
        viewPager2 = binding.viewPager
        viewPager2.offscreenPageLimit = 2
        viewPager2.adapter = MainPagerAdapter(requireActivity())
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                MainActivity.currentMainPage = position
            }
        })
        //tab 初始化
        tabLayout = binding.layoutTab
        TabLayoutMediator(
            tabLayout,
            viewPager2,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    //角色
                    0 -> {
                        tab.icon =
                            ResourcesCompat.getDrawable(resources, R.drawable.ic_character, null)
                        tab.text = sp.getInt(Constants.SP_COUNT_CHARACTER, 0).toString()
                    }
                    //装备
                    1 -> {
                        tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_equip, null)
                        tab.text = sp.getInt(Constants.SP_COUNT_EQUIP, 0).toString()
                    }
                    //怪物
                    2 -> {
                        tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_enemy, null)
                        tab.text = sp.getInt(Constants.SP_COUNT_ENEMY, 0).toString()
                    }
                }
            }).attach()

    }

    private fun setListener() {
        //点击已选择tab， 改变布局
        binding.layoutTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (tab == binding.layoutTab.getTabAt(1)) {
                    isList = !isList
                    EquipmentListFragment.viewModel.isList.postValue(isList)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })
        //功能模块
        MainActivity.fab.setOnClickListener {
            PopupMenuUtil.showPopupMenu(
                requireContext(),
                R.menu.menu_main,
                it,
                object : PopupMenuUtil.ItemClickListener {
                    override fun onClick(item: MenuItem?) {
                        when (item?.itemId) {
                            R.id.settingsFragment -> {
                                findNavController().navigate(R.id.action_containerFragment_to_settingsFragment)
                            }
                            R.id.search -> {
                                val layout: View =
                                    layoutInflater.inflate(R.layout.layout_search, null)
                                val dialog = MaterialAlertDialogBuilder(requireContext())
                                    .setView(layout)
                                    .create()
                                dialog.window?.setGravity(Gravity.BOTTOM)
                                dialog.show()
                                val searchView = layout.findViewById<SearchView>(R.id.search_input)
                                searchView.onActionViewExpanded()
                                searchView.isSubmitButtonEnabled = true
                                if (MainActivity.currentMainPage == 0) {
                                    searchView.queryHint = "角色名"
                                    searchView.setOnQueryTextListener(object :
                                        SearchView.OnQueryTextListener {
                                        override fun onQueryTextSubmit(query: String?): Boolean {
                                            CharacterListFragment.listAdapter.filter.filter(query)
                                            return false
                                        }

                                        override fun onQueryTextChange(newText: String?): Boolean {
                                            //重置
                                            if (newText == "" && CharacterListFragment.listAdapter.itemCount < 30) {
                                                sharedCharacterViewModel.getCharacters(
                                                    MainActivity.sortType,
                                                    MainActivity.sortAsc
                                                )
                                            }
                                            return false
                                        }
                                    })
                                } else {
                                    searchView.queryHint = "装备名"
                                    searchView.setOnQueryTextListener(object :
                                        SearchView.OnQueryTextListener {
                                        override fun onQueryTextSubmit(query: String?): Boolean {
                                            EquipmentListFragment.adapter.filter.filter(query)
                                            return false
                                        }

                                        override fun onQueryTextChange(newText: String?): Boolean {
                                            //重置
                                            if (newText == "" && EquipmentListFragment.adapter.itemCount < 50) {
                                                EquipmentListFragment.viewModel.getEquips()
                                            }
                                            return false
                                        }
                                    })
                                }
                            }

                        }
                    }
                })
        }
    }

    //配置共享元素动画
    private fun prepareTransitions() {

        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                if (MainActivity.currentCharaPosition != 0) binding.layoutToolbar.setExpanded(false)
                try {
                    if (names!!.isNotEmpty()) {
                        sharedElements ?: return
                        //角色列表
                        if (names.size > 0 && names[0].contains("img")) {
                            val vh =
                                CharacterListFragment.characterList.findViewHolderForAdapterPosition(
                                    MainActivity.currentCharaPosition
                                ) ?: return
                            val v0 =
                                vh.itemView.findViewById<AppCompatImageView>(R.id.character_pic)
                            val v1 =
                                vh.itemView.findViewById<ConstraintLayout>(R.id.content)
                            sharedElements[names[0]] = v0
                            sharedElements[names[1]] = v1
                        } else {
                            //装备列表
                            val euqipView =
                                EquipmentListFragment.list.findViewHolderForAdapterPosition(
                                    MainActivity.currentEquipPosition
                                ) ?: return
                            val ev0 =
                                euqipView.itemView.findViewById<AppCompatImageView>(R.id.item_pic)
                            sharedElements[names[0]] = ev0
                        }
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, e.message ?: "")
                }
            }

        })
    }
}
