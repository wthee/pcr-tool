package cn.wthee.pcrtool.ui.tool.equip

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.databinding.FragmentFilterEquipmentBinding
import cn.wthee.pcrtool.databinding.LayoutChipBinding
import cn.wthee.pcrtool.ui.common.CommonDialogFragment
import cn.wthee.pcrtool.utils.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * 装备筛选弹窗
 *
 * 页面布局 [FragmentFilterEquipmentBinding]
 *
 * ViewModels [EquipmentViewModel]
 */
class EquipmentFilterDialogFragment : CommonDialogFragment() {

    private lateinit var binding: FragmentFilterEquipmentBinding
    private val viewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterEquipmentBinding.inflate(layoutInflater, container, false)
        //筛选
        val chips = binding.chipsType
        initFilter(chips, binding)
        binding.reset.setOnClickListener {
            viewModel.reset.postValue(true)
            dialog?.dismiss()
        }
        binding.next.setOnClickListener {
            filterData(binding)
            dialog?.dismiss()
        }
        return binding.root
    }

    /**
     * 装备筛选
     */
    private fun filterData(binding: FragmentFilterEquipmentBinding) {
        //筛选选项
        val chip =
            binding.root.findViewById<Chip>(binding.chipsType.checkedChipId)
        EquipmentListFragment.equipFilterParams.type = chip.text.toString()
        EquipmentListFragment.equipName = binding.searchInput.text.toString()
        viewModel.getEquips(EquipmentListFragment.equipName)
    }

    /**
     * 初始化筛选
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initFilter(
        chips: ChipGroup,
        binding: FragmentFilterEquipmentBinding
    ) {
        //类型
        EquipmentListFragment.equipTypes.forEachIndexed { _, type ->
            val chip = LayoutChipBinding.inflate(layoutInflater).root
            chip.text = type
            chip.isCheckable = true
            chip.isClickable = true
            chips.addView(chip)
            if (EquipmentListFragment.equipFilterParams.type == type) {
                chip.isChecked = true
            }
        }
        //名字
        if (EquipmentListFragment.equipName != "") {
            binding.searchInput.setText(EquipmentListFragment.equipName)
        }
        //取消焦点
        binding.layoutFilter.setOnTouchListener { _, _ ->
            binding.searchInput.clearFocus()
            return@setOnTouchListener false
        }
    }
}