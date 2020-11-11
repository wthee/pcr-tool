package cn.wthee.pcrtool.ui.detail.equipment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentAttrAdapter
import cn.wthee.pcrtool.adapters.EquipmentMaterialAdapter
import cn.wthee.pcrtool.data.view.EquipmentMaxData
import cn.wthee.pcrtool.data.view.allNotZero
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EquipmentDetailsFragment : BottomSheetDialogFragment() {

    private val EQUIP = "equip"

    companion object {
        fun getInstance(equip: EquipmentMaxData) =
            EquipmentDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EQUIP, equip)
                }
            }

        //
        var materialClickPosition = -1
        lateinit var materialAdapter: EquipmentMaterialAdapter
    }

    private lateinit var equip: EquipmentMaxData
    private lateinit var binding: FragmentEquipmentDetailsBinding
    private lateinit var behavior: BottomSheetBehavior<View>
    private val viewModel by activityViewModels<EquipmentDetailsViewModel> {
        InjectorUtil.provideEquipmentDetailsViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            equip = it.getSerializable(EQUIP) as EquipmentMaxData
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEquipmentDetailsBinding.inflate(inflater, container, false)
        init()
        setObserve()
        viewModel.getEquipInfos(equip)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        materialClickPosition = -1
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val v: View = FragmentEquipmentDetailsBinding.inflate(layoutInflater).root
        dialog.setContentView(v)

        val layoutParams = (v.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        behavior = layoutParams.behavior as BottomSheetBehavior<View>
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.apply {
            //toolbar
            ToolbarUtil(toolbar).setCenterTitle(equip.equipmentName)
                .leftIcon.setOnClickListener {
                    goBack()
                }
            detail.apply {
                //图标
                val picUrl = Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEBP
                itemPic.load(picUrl) {
                    error(R.drawable.unknow_gray)
                }
                //描述
                desc.text = equip.getDesc()
                //属性词条
                val adapter = EquipmentAttrAdapter()
                attrs.adapter = adapter
                adapter.submitList(equip.attr.allNotZero())
            }
            //动态限制只有一个列表可滚动
            material.setOnTouchListener { _, _ ->
                if (!material.isNestedScrollingEnabled) material.isNestedScrollingEnabled = true
                if (drops.isNestedScrollingEnabled) drops.isNestedScrollingEnabled = false
                return@setOnTouchListener false
            }
        }
    }

    private fun setObserve() {
        viewModel.equipMaterialInfos.observe(viewLifecycleOwner, {
            //合成素材
            if (it.isNotEmpty()) {
                binding.materialCount.text = getString(R.string.title_material, it.size)
                materialAdapter = EquipmentMaterialAdapter(binding, behavior, viewModel)
                binding.material.adapter = materialAdapter
                materialAdapter.submitList(it)
            } else {
                binding.material.visibility = View.GONE
            }
        })
    }

    private fun goBack() {
        dialog?.dismiss()
    }

}