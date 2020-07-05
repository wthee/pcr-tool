package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity.Companion.sortAsc
import cn.wthee.pcrtool.MainActivity.Companion.sortType
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterListBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.COLUMN_COUNT
import cn.wthee.pcrtool.utils.InjectorUtil
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout


class CharacterListFragment : Fragment() {

    companion object {
        lateinit var viewModel: CharacterViewModel
        lateinit var characterList: RecyclerView
        lateinit var listAdapter: CharacterAdapter
        var filterFlag = 0
    }

    private lateinit var binding: FragmentCharacterListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        binding.layoutRefresh.setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
        viewModel = InjectorUtil.provideCharacterViewModelFactory()
            .create(CharacterViewModel::class.java)
        //加载数据
        init()
        //监听数据变化
        setObserve()
        //控件监听
        setListener()
        return binding.root
    }

    //加载数据
    private fun init() {
        viewModel.getCharacters(sortType, sortAsc)
        listAdapter = CharacterAdapter(this@CharacterListFragment)
        characterList = binding.characterList
        binding.characterList.apply {
            adapter =
                listAdapter
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
            val lm = GridLayoutManager(MyApplication.getContext(), COLUMN_COUNT)
            lm.orientation = GridLayoutManager.VERTICAL
            layoutManager = lm
        }
    }

    //绑定observe
    private fun setObserve() {
        viewModel.apply {
            //角色
            characters.observe(viewLifecycleOwner, Observer { data ->
                if (data != null) {
                    listAdapter.submitList(data) {
                        if (filterFlag.toString() != "0") {
                            listAdapter.filter.filter(filterFlag.toString())
                        }
                        sp.edit {
                            putInt(Constants.SP_COUNT_CHARACTER, data.size)
                        }
                        ContainerFragment.tabLayout.getTabAt(0)?.text = data.size.toString()
                    }
                }
            })
            //刷新
            refresh.observe(viewLifecycleOwner, Observer {
                binding.layoutRefresh.isRefreshing = it
            })
            //加载
            loading.observe(viewLifecycleOwner, Observer {
                binding.characterProgress.visibility = if (it) View.VISIBLE else View.GONE
            })
            //重新加载
            reload.observe(viewLifecycleOwner, Observer {
                try {
                    findNavController().navigate(
                        R.id.action_containerFragment_self,
                        null,
                        NavOptions.Builder().setPopUpTo(R.id.action_containerFragment_self, false)
                            .build()
                    )
                } catch (e: Exception) {
                }
            })
        }
    }

    //控件监听
    private fun setListener() {
        binding.apply {
            //下拉刷新
            layoutRefresh.setOnRefreshListener {
                viewModel.getCharacters(sortType, sortAsc)
            }

            //滑动时暂停glide加载
            characterList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Glide.with(root.context).resumeRequests()
                    } else {
                        Glide.with(root.context).pauseRequests()
                    }
                }
            })
            //筛选
            filterTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    filterFlag = tab?.position ?: 0
                    viewModel.getCharacters(sortType, sortAsc)
                }
            })
            //排序
            sortTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    sortType = tab?.position ?: 0
                    viewModel.getCharacters(sortType, sortAsc)
                    binding.characterList.smoothScrollToPosition(0)
                }
            })

        }
    }
}
