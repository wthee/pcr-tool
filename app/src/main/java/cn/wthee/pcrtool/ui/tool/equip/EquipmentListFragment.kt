package cn.wthee.pcrtool.ui.tool.equip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CallBack
import cn.wthee.pcrtool.adapter.EquipmentListAdapter
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.data.view.EquipmentMaxData
import cn.wthee.pcrtool.databinding.FragmentEquipmentListBinding
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 装备列表
 *
 * 页面布局 [FragmentEquipmentListBinding]
 *
 * ViewModels [EquipmentViewModel]
 */
class EquipmentListFragment : Fragment() {

    companion object {
        lateinit var list: RecyclerView
        lateinit var motionLayout: MotionLayout
        var equipFilterParams = FilterEquipment(true, "全部")
        lateinit var equipTypes: ArrayList<String>
        lateinit var adapter: EquipmentListAdapter
        var equipName = ""
    }

    private lateinit var binding: FragmentEquipmentListBinding
    private val viewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentEquipmentListBinding.inflate(inflater, container, false)
        init()
        setListener()
        //绑定观察
        setObserve()
        return binding.root
    }

    private fun setListener() {
        //重置
        binding.equipCount.setOnLongClickListener {
            reset()
            return@setOnLongClickListener true
        }
        //筛选、搜索
        binding.equipCount.setOnClickListener {
            EquipmentFilterDialogFragment().show(parentFragmentManager, "filter_character")
        }
    }

    private fun init() {
        motionLayout = binding.root
        list = binding.toolList
        //设置头部
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_equip,
            getString(R.string.tool_equip)
        )
        binding.apply {
            adapter = EquipmentListAdapter(object : CallBack {
                override fun todo(data: Any?) {
                    data?.let {
                        //点击事件回调
                        EquipmentDetailsDialogFragment.getInstance(it as EquipmentMaxData)
                            .show(parentFragmentManager, "equip_details")
                    }
                }
            })
            binding.toolList.adapter = adapter
        }
        //获取装备类型
        equipTypes = arrayListOf()
        viewLifecycleOwner.lifecycleScope.launch {
            equipTypes.add("全部")
            viewModel.getTypes().forEach {
                equipTypes.add(it)
            }
        }
        load(false)
    }

    private fun reset() {
        equipFilterParams.initData()
        equipName = ""
        load(true)
    }


    private fun load(reload: Boolean) {
        lifecycleScope.launch {
            //获取角色
            DataStoreUtil.get(Constants.SP_STAR_EQUIP).collect { str ->
                val newStarIds = DataStoreUtil.fromJson<ArrayList<Int>>(str)
                equipFilterParams.starIds = newStarIds ?: arrayListOf()
                viewModel.getEquips(equipFilterParams, "", reload)
            }
        }
    }

    private fun setObserve() {
        //装备数量
        if (!viewModel.equipmentCounts.hasObservers()) {
            viewModel.equipmentCounts.observe(viewLifecycleOwner) {
                binding.equipCount.text = it.toString()
            }
        }
        //装备信息
        if (!viewModel.updateEquip.hasActiveObservers()) {
            viewModel.updateEquip.observe(viewLifecycleOwner) {
                //装备信息
                lifecycleScope.launch {
                    @OptIn(ExperimentalCoroutinesApi::class)
                    viewModel.equipments.collectLatest { data ->
                        adapter.submitData(data)
                    }
                }
            }
        }
        //重置
        if (!viewModel.reset.hasActiveObservers()) {
            viewModel.reset.observe(viewLifecycleOwner) {
                reset()
            }
        }
    }
}