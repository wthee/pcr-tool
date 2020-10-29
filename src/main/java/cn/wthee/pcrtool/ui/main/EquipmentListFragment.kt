package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainPagerFragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentAdapter
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.databinding.FragmentEquipmentListBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import kotlinx.coroutines.launch


class EquipmentListFragment : Fragment() {

    companion object {
        lateinit var list: RecyclerView
        lateinit var listAdapter: EquipmentAdapter
        var equipFilterParams = FilterEquipment(true, "全部")
        var asc = false
        lateinit var equipTypes: ArrayList<String>
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
        init()
        //设置监听
        setListener()
        //绑定观察
        setObserve()
        loadData()
        //获取装备类型
        //公会列表
        equipTypes = arrayListOf()
        viewLifecycleOwner.lifecycleScope.launch {
            equipTypes.add("全部")
            viewModel.getTypes().forEach {
                equipTypes.add(it.type)
            }
        }
        return binding.root
    }

    private fun init() {
        binding.apply {
            layoutRefresh.setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
            list = recycler
            listAdapter = EquipmentAdapter(parentFragmentManager)
            recycler.adapter = listAdapter
        }
    }

    private fun setObserve() {
        viewModel.apply {
            //获取信息
            if (!equipments.hasObservers()) {
                equipments.observe(viewLifecycleOwner, Observer { data ->
                    if (data != null && data.isNotEmpty()) {
                        MainPagerFragment.tipText.visibility = View.GONE
                        listAdapter.submitList(data) {
                            listAdapter.filter.filter(equipFilterParams.toJsonString())
                            MainActivity.sp.edit {
                                putInt(Constants.SP_COUNT_EQUIP, data.size)
                            }
                            MainPagerFragment.tabLayout.getTabAt(1)?.text = data.size.toString()
                        }
                    } else {
                        MainPagerFragment.tipText.visibility = View.VISIBLE
                    }
                })
            }
//            val pageAdapter = EquipmentPageAdapter(parentFragmentManager)
//            binding.equipPage.adapter = pageAdapter
//
//            lifecycleScope.launch {
//                @OptIn(ExperimentalCoroutinesApi::class)
//                viewModel.allEquips.collectLatest { pageAdapter.submitData(it) }
//            }

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
            //下拉刷新
            layoutRefresh.setOnRefreshListener {
                equipFilterParams.initData()
                loadData()
                layoutRefresh.isRefreshing = false
            }

        }
    }

    private fun loadData() {
        viewModel.getEquips(asc, "")
    }
}