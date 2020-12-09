package cn.wthee.pcrtool.ui.home

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
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.databinding.FragmentEquipmentListBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ResourcesUtil
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
    }

    private lateinit var binding: FragmentEquipmentListBinding
    private val viewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEquipmentListBinding.inflate(inflater, container, false)
        binding.apply {
            list = binding.equipPage
            pageAdapter = EquipmentPageAdapter(parentFragmentManager)
            binding.equipPage.adapter = pageAdapter
            //重置
            equipReset.apply {
                setProgressBackgroundColorSchemeColor(ResourcesUtil.getColor(R.color.colorWhite))
                setColorSchemeResources(R.color.colorPrimary)
                setOnRefreshListener {
                    reset()
                }
            }
        }
        //绑定观察
        setObserve()
        //获取装备类型
        equipTypes = arrayListOf()
        viewLifecycleOwner.lifecycleScope.launch {
            equipTypes.add("全部")
            viewModel.getTypes().forEach {
                equipTypes.add(it)
            }
        }
        viewModel.getEquips("")
        return binding.root
    }

    private fun reset() {
        equipFilterParams.initData()
        equipFilterParams.all = true
        viewModel.getEquips("")
        binding.equipReset.isRefreshing = false
    }

    private fun setObserve() {

        //装备数量
        if (!viewModel.equipmentCounts.hasObservers()) {
            viewModel.equipmentCounts.observe(viewLifecycleOwner, {
                MainActivity.sp.edit {
                    putInt(Constants.SP_COUNT_EQUIP, it)
                }
                MainPagerFragment.tabLayout.getTabAt(1)?.text = it.toString()
                MainPagerFragment.tipText.visibility = if (it > 0) View.GONE else View.VISIBLE
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
                        binding.loading.root.visibility = View.GONE
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