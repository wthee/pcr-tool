package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.spSetting
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentAdapter
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.databinding.FragmentEquipmentListBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import com.bumptech.glide.Glide


class EquipmentListFragment : Fragment() {

    companion object {
        lateinit var list: RecyclerView
        lateinit var listAdapter: EquipmentAdapter
        var isList = true
        var equipfilterParams = FilterEquipment(true, true, true)
        var asc = false
    }

    private lateinit var binding: FragmentEquipmentListBinding
    private val viewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEquipmentListBinding.inflate(inflater, container, false)
        isList = spSetting.getBoolean("equip_is_list", true)
        init(isList)
        //设置监听
        setListener()
        //绑定观察
        setObserve()
        loadData()
        return binding.root
    }

    private fun init(isList: Boolean) {
        binding.apply {
            layoutRefresh.setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
            list = recycler
            if (isList) {
                val linearLayoutManager = LinearLayoutManager(MyApplication.getContext())
                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                recycler.layoutManager = linearLayoutManager
            } else {
                val gridLayoutManager =
                    GridLayoutManager(MyApplication.getContext(), Constants.COLUMN_COUNT_EQUIP)
                gridLayoutManager.orientation = GridLayoutManager.VERTICAL
                recycler.layoutManager = gridLayoutManager
            }
            listAdapter = EquipmentAdapter(isList)
            recycler.adapter = listAdapter
            recycler.setItemViewCacheSize(100)
        }
    }

    private fun setObserve() {
        viewModel.apply {
            //加载
            if (!isLoading.hasObservers()) {
                isLoading.observe(viewLifecycleOwner, Observer {
//                    MainPagerFragment.progress.visibility = if (it) View.VISIBLE else View.GONE
                })
            }
            if (!isList.hasObservers()) {
                isList.observe(viewLifecycleOwner, Observer {
                    init(it)
                    loadData()
                })
            }
            //获取信息
            if (!equipments.hasObservers()) {
                equipments.observe(viewLifecycleOwner, Observer { data ->
                    if (data != null && data.isNotEmpty()) {
                        binding.noDataTip.visibility = View.GONE
                        listAdapter.submitList(data) {
                            listAdapter.filter.filter(equipfilterParams.toJsonString())
                            MainActivity.sp.edit {
                                putInt(Constants.SP_COUNT_EQUIP, data.size)
                            }
                            MainPagerFragment.tabLayout.getTabAt(1)?.text = data.size.toString()
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
        }
    }

    private fun setListener() {
        binding.apply {
            //滑动时暂停glide加载
            recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Glide.with(root.context).resumeRequests()
                    } else {
                        Glide.with(root.context).pauseRequests()
                    }
                }
            })
            //下拉刷新
            layoutRefresh.setOnRefreshListener {
                equipfilterParams.initData()
                loadData()
            }

        }
    }

    private fun loadData() {
        viewModel.getEquips(asc, "")
    }
}