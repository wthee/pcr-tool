package cn.wthee.pcrtool.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.SharedElementCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.MainPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentMainPagerBinding
import cn.wthee.pcrtool.ui.detail.character.CharacterBasicInfoFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.button.MaterialButton
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
            CharacterListFragment.characterList.scrollToPosition(MainActivity.currentCharaPosition)
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        binding.progress.visibility = View.GONE
        MainActivity.fab.visibility = View.VISIBLE
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
        //功能模块
        MainActivity.fab.setOnClickListener {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.layout_function_list, null)
            val popupWindow = PopupWindow(
                view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popupWindow.isOutsideTouchable = true
            popupWindow.isFocusable = true
            popupWindow.animationStyle = R.style.PopUpAnimation
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            popupWindow.showAtLocation(it, Gravity.END or Gravity.BOTTOM, 0, 0)
            val setting = view.findViewById<MaterialButton>(R.id.setting)
            setting.setOnClickListener {
                findNavController().navigate(R.id.action_containerFragment_to_settingsFragment)
                popupWindow.dismiss()
            }
//            PopupMenuUtil.showPopupMenu(
//                requireContext(),
//                R.menu.menu_main,
//                it,
//                filterParams == "1",
//                object : PopupMenuUtil.ItemClickListener {
//                    override fun onClick(item: MenuItem?) {
//                        when (item?.itemId) {
//                            R.id.settingsFragment -> {
//                                findNavController().navigate(R.id.action_containerFragment_to_settingsFragment)
//                            }
//                            R.id.love -> {
//                                filterParams = if (filterParams == "1") {
//                                    ToastUtil.short("显示全部")
//                                    "0"
//                                } else {
//                                    ToastUtil.short("仅显示收藏")
//                                    "1"
//                                }
//                                when (MainActivity.currentMainPage) {
//                                    0 -> {
//                                        CharacterListFragment.viewModel.getCharacters(
//                                            sortType,
//                                            sortAsc, "", mapOf()
//                                        )
//                                        CharacterListFragment.listAdapter.notifyDataSetChanged()
//                                    }
//                                    1 -> {
//                                        CharacterListFragment.listAdapter.notifyDataSetChanged()
//                                    }
//                                    2 -> {
//                                        CharacterListFragment.listAdapter.notifyDataSetChanged()
//                                    }
//                                }
//
//
//                            }
//                            R.id.search -> {
//                                //显示搜索布局
//                                val layout = layoutInflater.inflate(R.layout.layout_search, null)
//                                val dialog = MaterialAlertDialogBuilder(requireContext())
//                                    .setView(layout)
//                                    .create()
//                                dialog.window?.setGravity(Gravity.BOTTOM)
//                                dialog.show()
//                                //搜索框
//                                val searchView = layout.findViewById<SearchView>(R.id.search_input)
//                                searchView.onActionViewExpanded()
//                                searchView.isSubmitButtonEnabled = true
//                                when (MainActivity.currentMainPage) {
//                                    0 -> searchView.queryHint = "角色名"
//                                    1 -> searchView.queryHint = "装备名"
//                                    2 -> searchView.queryHint = "怪物名"
//                                }
//                                searchView.setOnQueryTextListener(object :
//                                    SearchView.OnQueryTextListener {
//                                    override fun onQueryTextSubmit(query: String?): Boolean {
//                                        when (MainActivity.currentMainPage) {
//                                            0 -> {
//                                                //角色名字
//                                                CharacterListFragment.viewModel.getCharacters(
//                                                    sortType,
//                                                    sortAsc, query ?: "", mapOf()
//                                                )
//                                            }
//                                            1 -> EquipmentListFragment.listAdapter.filter.filter(
//                                                query
//                                            )
//                                            2 -> EnemyListFragment.listAdapter.filter.filter(
//                                                query
//                                            )
//                                        }
//
//                                        return false
//                                    }
//
//                                    override fun onQueryTextChange(newText: String?): Boolean {
//                                        //重置
//                                        if (newText == "") {
//                                            when (MainActivity.currentMainPage) {
//                                                0 -> CharacterListFragment.viewModel.getCharacters(
//                                                    sortType,
//                                                    sortAsc, "", mapOf()
//                                                )
//                                                1 -> EquipmentListFragment.viewModel.getEquips()
//                                                2 -> EnemyListFragment.viewModel.getAllEnemy()
//                                            }
//                                        }
//
//                                        return false
//                                    }
//                                })
//                            }
//                            R.id.filter -> {
//                                val layout = layoutInflater.inflate(R.layout.layout_filter, null)
//                                val dialog = MaterialAlertDialogBuilder(requireContext())
//                                    .setView(layout)
//                                    .create()
//                                dialog.window?.setGravity(Gravity.BOTTOM)
//                                dialog.show()
//                                //位置筛选
//                                val checkChara1 =
//                                    layout.findViewById<MaterialCheckBox>(R.id.checkbox_chara_1)
//                                val checkChara2 =
//                                    layout.findViewById<MaterialCheckBox>(R.id.checkbox_chara_2)
//                                val checkChara3 =
//                                    layout.findViewById<MaterialCheckBox>(R.id.checkbox_chara_3)
//                                val next = layout.findViewById<MaterialButton>(R.id.next)
//                                //初始值
//                                val ps = filterParams.split(":")
//                                if (ps.isNotEmpty() && ps.size == 5) {
//                                    checkChara1.isChecked = ps[1] == "1"
//                                    checkChara2.isChecked = ps[2] == "1"
//                                    checkChara3.isChecked = ps[3] == "1"
//                                }
//                                next.setOnClickListener {
//                                    dialog.dismiss()
//                                    filterParams = "position:"
//                                    filterParams += if (checkChara1.isChecked) "1:" else "0:"
//                                    filterParams += if (checkChara2.isChecked) "1:" else "0:"
//                                    filterParams += if (checkChara3.isChecked) "1:" else "0:"
//                                    CharacterListFragment.viewModel.getCharacters(
//                                        sortType,
//                                        sortAsc, "", mapOf()
//                                    )
//                                }
//                            }
//                        }
//                    }
//                })
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
                //返回时隐藏toolbar
                binding.layoutToolbar.setExpanded(false)
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
