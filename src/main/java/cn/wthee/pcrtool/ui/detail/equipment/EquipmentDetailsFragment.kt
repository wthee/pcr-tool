package cn.wthee.pcrtool.ui.detail.equipment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentAttrAdapter
import cn.wthee.pcrtool.adapters.EquipmentMaterialAdapter
import cn.wthee.pcrtool.data.model.entity.EquipmentData
import cn.wthee.pcrtool.data.model.entity.EquipmentMaxData
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

        lateinit var viewModel: EquipmentDetailsViewModel
        lateinit var materialAdapter: EquipmentMaterialAdapter
    }

    private lateinit var equip: EquipmentMaxData
    private lateinit var binding: FragmentEquipmentDetailsBinding
    private lateinit var cusToolbar: ToolbarUtil
    private lateinit var behavior: BottomSheetBehavior<View>


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
        viewModel = InjectorUtil.provideEquipmentDetailsViewModelFactory()
            .create(EquipmentDetailsViewModel::class.java)
        init()
        setObserve()
        viewModel.getEquipInfos(equip)
        return binding.root
    }


    override fun setupDialog(dialog: Dialog, style: Int) {
        val v: View = FragmentEquipmentDetailsBinding.inflate(layoutInflater).root
        dialog.setContentView(v)

        val layoutParams = (v.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        behavior = layoutParams.behavior as BottomSheetBehavior<View>
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun init() {
        binding.apply {
            progressBar1.visibility = View.VISIBLE
            //toolbar
            cusToolbar = ToolbarUtil(toolbar)
            cusToolbar.apply {
                setLeftIcon(R.drawable.ic_back)
                hideRightIcon()
                setTitleColor(R.color.colorPrimary)
                setBackground(R.color.colorWhite)
                setTitleCenter()
                title.text = equip.equipmentName
                leftIcon.setOnClickListener {
                    goBack()
                }
            }
            binding.detail.apply {
                //图标
                val picUrl = Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEBP
                itemPic.load(picUrl) {
                    error(R.drawable.error)
                }
                //描述
                desc.text = equip.getDesc()
                //属性词条
                val adapter = EquipmentAttrAdapter()
                attrs.adapter = adapter
                adapter.submitList(equip.getAttrs())
            }
        }
    }

    private fun setObserve() {
        viewModel.equipMaterialInfos.observe(viewLifecycleOwner, Observer {
            //合成素材
            if (it.isNotEmpty()) {
                materialAdapter = EquipmentMaterialAdapter(binding, behavior)
                binding.material.adapter = materialAdapter
                materialAdapter.submitList(it) {
                    binding.progressBar1.visibility = View.INVISIBLE
                }
            } else {
                binding.material.visibility = View.GONE
            }
        })
    }

    private fun goBack() {
        dialog?.dismiss()
    }

}