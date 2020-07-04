package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.MainPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentContainerBinding
import cn.wthee.pcrtool.ui.main.EquipmentListFragment.Companion.isList
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textview.MaterialTextView


class ContainerFragment : Fragment() {

    companion object {
        var cListClick = false
        lateinit var tabLayout: TabLayout
    }

    private lateinit var binding: FragmentContainerBinding
    private lateinit var viewPager2: ViewPager2
    lateinit var search: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContainerBinding.inflate(inflater, container, false)
        search = binding.search
        cListClick = false
        //toolbar
        setHasOptionsMenu(false)
        val toolbar = binding.toolbar
        requireActivity().setActionBar(toolbar)
        init()
        prepareTransitions()
        return binding.root
    }

    //加载viewpager
    private fun init() {
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
        tabLayout = binding.layoutTab
        TabLayoutMediator(
            tabLayout,
            viewPager2,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> {
                        tab.icon = resources.getDrawable(R.drawable.notice, null)
                        tab.text = sp.getInt(Constants.SP_COUNT_CHARACTER, 0).toString()
                    }
                    1 -> {
                        tab.icon = resources.getDrawable(R.drawable.equip, null)
                        tab.text = sp.getInt(Constants.SP_COUNT_EQUIP, 0).toString()
                    }
                }
            }).attach()
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
        //搜索
        search.setOnClickListener {
            val layout: View = layoutInflater.inflate(R.layout.layout_search, null)
            val dialog = MaterialAlertDialogBuilder(context)
                .setView(layout)
                .create()
            dialog.window?.setGravity(Gravity.BOTTOM)
            dialog.show()
            val searchView = layout.findViewById<SearchView>(R.id.search_input)
            searchView.onActionViewExpanded()
            searchView.isSubmitButtonEnabled = true
            if (MainActivity.currentMainPage == 0) {
                searchView.queryHint = "角色名"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        CharacterListFragment.listAdapter.filter.filter(query)
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        //重置
                        if (newText == "" && CharacterListFragment.listAdapter.itemCount < 30) {
                            CharacterListFragment.viewModel.getCharacters(
                                MainActivity.sortType,
                                MainActivity.sortAsc
                            )
                        }
                        return false
                    }
                })
            } else {
                searchView.queryHint = "装备名"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    //配置共享元素动画
    private fun prepareTransitions() {

        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                try {
                    if (names!!.isNotEmpty()) {
                        sharedElements ?: return
                        //角色列表
                        if (names.size > 0 && names[0].contains("img")) {
                            val vh =
                                CharacterListFragment.characterList.findViewHolderForAdapterPosition(
                                    MainActivity.currentCharaPosition
                                ) ?: return
                            val v1 =
                                vh.itemView.findViewById<AppCompatImageView>(R.id.character_pic)
                            sharedElements[names[0]] = v1
                        } else {
                            //装备列表
                            val euqipView =
                                EquipmentListFragment.list.findViewHolderForAdapterPosition(
                                    MainActivity.currentEquipPosition
                                ) ?: return
                            val ev1 =
                                euqipView.itemView.findViewById<AppCompatImageView>(R.id.item_pic)
                            val ev2 =
                                euqipView.itemView.findViewById<MaterialTextView>(R.id.name)
                            sharedElements[names[0]] = ev1
                            sharedElements[names[1]] = ev2
                        }
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, e.message ?: "")
                }
            }
        })
    }

}
