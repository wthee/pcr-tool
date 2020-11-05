package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.adapters.EquipmentPageAdapter
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.database.view.EquipmentMaxData
import cn.wthee.pcrtool.databinding.FragmentEquipmentListBinding
import cn.wthee.pcrtool.utils.InjectorUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class EquipmentListFragment : Fragment() {

    companion object {
        lateinit var list: RecyclerView
        var equipFilterParams = FilterEquipment(true, "全部")
        var asc = false
        lateinit var equipTypes: ArrayList<String>
        lateinit var pageAdapter: EquipmentPageAdapter
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

            pageAdapter = EquipmentPageAdapter(parentFragmentManager)
            binding.equipPage.adapter = pageAdapter
        }
    }

    private fun setObserve() {
        viewModel.apply {
            lifecycleScope.launch {
                @OptIn(ExperimentalCoroutinesApi::class)
                viewModel.allEquips.collectLatest {
                    submitData(it)
                }
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            viewModel.allEquips.collectLatest {
                submitData(it)
            }
        }
    }

    private suspend fun submitData(it: PagingData<EquipmentMaxData>) {
        pageAdapter.submitData(it.filterSync {
            if (!equipFilterParams.all) {
                //过滤非收藏角色
                if (!MainActivity.sp.getBoolean(it.equipmentId.toString(), false)) {
                    return@filterSync false
                }
            }
            //种类筛选
            if (equipFilterParams.type != "全部") {
                if (equipFilterParams.type != it.type) {
                    return@filterSync false
                }
            }
            return@filterSync true
        })
        pageAdapter.notifyDataSetChanged()
    }
}