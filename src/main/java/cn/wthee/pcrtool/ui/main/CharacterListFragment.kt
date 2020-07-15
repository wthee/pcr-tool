package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity.Companion.sortAsc
import cn.wthee.pcrtool.MainActivity.Companion.sortType
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterAdapter
import cn.wthee.pcrtool.data.model.FilterDataCharacter
import cn.wthee.pcrtool.databinding.FragmentCharacterListBinding
import cn.wthee.pcrtool.databinding.LayoutWarnDialogBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import cn.wthee.pcrtool.utils.DialogUtil
import cn.wthee.pcrtool.utils.InjectorUtil
import com.bumptech.glide.Glide


class CharacterListFragment : Fragment() {

    companion object {
        lateinit var characterList: RecyclerView
        lateinit var listAdapter: CharacterAdapter
        var filterParams = FilterDataCharacter(true, true, true, true)
        lateinit var handler: Handler
    }

    private lateinit var binding: FragmentCharacterListBinding
    private val viewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        binding.layoutRefresh.setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
        //加载数据
        init()
        //监听数据变化
        setObserve()
        //控件监听
        setListener()
        viewModel.getCharacters(sortType, sortAsc, "", mapOf())
        handler = Handler(Handler.Callback {
            when (it.what) {
                0 -> {
                    val layout = LayoutWarnDialogBinding.inflate(layoutInflater)
                    DialogUtil.create(
                        requireContext(),
                        layout,
                        "访问出错",
                        Constants.NOTICE_TOAST_TIMEOUT
                    ).show()
                }
                1 -> viewModel.reload.postValue(true)
            }

            return@Callback true
        })
        return binding.root
    }

    //加载数据
    private fun init() {
        listAdapter = CharacterAdapter(this@CharacterListFragment)
        characterList = binding.characterList
        binding.characterList.apply {
            adapter = listAdapter
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }


    //绑定observe
    private fun setObserve() {
        viewModel.apply {
            //角色
            if (!characters.hasObservers()) {
                characters.observe(viewLifecycleOwner, Observer { data ->
                    if (data != null && data.isNotEmpty()) {
                        binding.noDataTip.visibility = View.GONE
                        listAdapter.submitList(data) {
                            listAdapter.filter.filter(filterParams.toJsonString())
                            sp.edit {
                                putInt(Constants.SP_COUNT_CHARACTER, data.size)
                            }
                            MainPagerFragment.tabLayout.getTabAt(0)?.text = data.size.toString()
                        }
                    } else {
                        binding.noDataTip.visibility = View.VISIBLE
                    }
                })
            }
            //刷新
            if (!refresh.hasObservers()) {
                refresh.observe(viewLifecycleOwner, Observer {
                    binding.layoutRefresh.isRefreshing = it
                })
            }
            //加载
            if (!isLoading.hasObservers()) {
                isLoading.observe(viewLifecycleOwner, Observer {
                    MainPagerFragment.progress.visibility = if (it) View.VISIBLE else View.GONE
                })
            }
            //重新加载
            if (!reload.hasObservers()) {
                reload.observe(viewLifecycleOwner, Observer {
                    try {
                        findNavController().popBackStack(R.id.containerFragment, true)
                        findNavController().navigate(R.id.containerFragment)
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, e.message.toString())
                    }
                })
            }
        }
    }

    //控件监听
    private fun setListener() {
        binding.apply {
            //下拉刷新
            layoutRefresh.setOnRefreshListener {
                filterParams.initData()
                viewModel.getCharacters(sortType, sortAsc, "", mapOf())
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

//            //排序
//            sortTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabReselected(tab: TabLayout.Tab?) {
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab?) {
//                }
//
//                override fun onTabSelected(tab: TabLayout.Tab?) {
//                    sortType = tab?.position ?: 0
//                    viewModel.getCharacters(sortType, sortAsc)
//                    binding.characterList.smoothScrollToPosition(0)
//                }
//            })
        }
    }
}
