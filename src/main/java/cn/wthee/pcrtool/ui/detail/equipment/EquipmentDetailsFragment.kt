package cn.wthee.pcrtool.ui.detail.equipment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentAttrAdapter
import cn.wthee.pcrtool.adapters.EquipmentMaterialAdapter
import cn.wthee.pcrtool.data.model.entity.EquipmentData
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GlideUtil
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


private const val EQUIP = "equip"
private const val DIALOG = "dialog"


class EquipmentDetailsFragment : BottomSheetDialogFragment() {

    companion object {
        fun getInstance(equip: EquipmentData, isDialog: Boolean) =
            EquipmentDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EQUIP, equip)
                    putBoolean(DIALOG, isDialog)
                }
            }

        lateinit var viewModel: EquipmentDetailsViewModel
        lateinit var materialAdapter: EquipmentMaterialAdapter
    }

    private lateinit var equip: EquipmentData
    private var isDialog: Boolean = false
    private lateinit var binding: FragmentEquipmentDetailsBinding
    private lateinit var cusToolbar: ToolbarUtil


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            equip = it.getSerializable(EQUIP) as EquipmentData
            isDialog = it.getBoolean(DIALOG)
        }
        if (!isDialog) {
            sharedElementEnterTransition =
                TransitionInflater.from(context).inflateTransition(android.R.transition.move)
            sharedElementReturnTransition =
                TransitionInflater.from(context).inflateTransition(android.R.transition.move)
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
        val behavior = layoutParams.behavior as BottomSheetBehavior<View>
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun init() {
        binding.apply {
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
            //共享元素
            itemPic.transitionName = "pic_${equip.equipmentId}"
            //图标
            val picUrl = Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEBP
            GlideUtil.load(picUrl, itemPic, R.drawable.error, parentFragment)
            //描述
            desc.text = equip.getDesc()
            //属性词条
            object : LinearLayoutManager(MyApplication.getContext()) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }.also {
                it.orientation = LinearLayoutManager.VERTICAL
                attrs.layoutManager = it
            }
            val adapter = EquipmentAttrAdapter()
            attrs.adapter = adapter
            adapter.submitList(equip.getAttrs())
        }
    }

    private fun setObserve() {
        viewModel.equipMaterialInfos.observe(viewLifecycleOwner, Observer {
            //合成素材
            if (it.isNotEmpty()) {
                materialAdapter = EquipmentMaterialAdapter(binding)
                binding.material.adapter = materialAdapter
                materialAdapter.submitList(it)
            } else {
                binding.material.visibility = View.GONE
            }
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
//            binding.equipProgressBar.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun goBack() {
        if (isDialog) {
            dialog?.dismiss()
        } else {
            view?.findNavController()?.navigateUp()
        }
    }

}