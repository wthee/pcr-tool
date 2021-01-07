package cn.wthee.pcrtool.ui.tool.equip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.EquipmentPageAdapter
import cn.wthee.pcrtool.data.bean.FilterEquipment
import cn.wthee.pcrtool.databinding.FragmentEquipmentListBinding
import cn.wthee.pcrtool.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 装备列表
 */
class EquipmentListFragment : Fragment() {

    companion object {
        lateinit var list: RecyclerView
        var equipFilterParams = FilterEquipment(true, "全部")
        var asc = false
        lateinit var equipTypes: ArrayList<String>
        lateinit var pageAdapter: EquipmentPageAdapter
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
        //设置头部
        ToolbarUtil(binding.toolEquip).setMainToolbar(
            R.drawable.ic_equip,
            getString(R.string.tool_equip)
        )
        binding.apply {
            list = binding.equipPage
            pageAdapter = EquipmentPageAdapter(parentFragmentManager)
            binding.equipPage.adapter = pageAdapter
        }
        //获取装备类型
        equipTypes = arrayListOf()
        viewLifecycleOwner.lifecycleScope.launch {
            equipTypes.add("全部")
            viewModel.getTypes().forEach {
                equipTypes.add(it)
            }
        }
        viewModel.getEquips("")
    }

    private fun reset() {
        equipFilterParams.initData()
        equipFilterParams.all = true
        equipName = ""
        viewModel.getEquips(equipName)
    }

    private fun setObserve() {

        //装备数量
        if (!viewModel.equipmentCounts.hasObservers()) {
            viewModel.equipmentCounts.observe(viewLifecycleOwner, {
                MainActivity.sp.edit {
                    putInt(Constants.SP_COUNT_EQUIP, it)
                }
                binding.equipCount.text = it.toString()
            })
        }
        //装备信息
        if (!viewModel.updateEquip.hasActiveObservers()) {
            viewModel.updateEquip.observe(viewLifecycleOwner, {
                //装备信息
                lifecycleScope.launch {
                    @OptIn(ExperimentalCoroutinesApi::class)
                    viewModel.equipments.collectLatest { data ->
                        pageAdapter.submitData(data)
                        pageAdapter.notifyDataSetChanged()
                    }
                }
            })
        }
        //重置
        if (!viewModel.reset.hasActiveObservers()) {
            viewModel.reset.observe(viewLifecycleOwner, {
                reset()
            })
        }
    }
}