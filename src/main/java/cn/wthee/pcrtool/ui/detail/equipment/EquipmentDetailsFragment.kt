package cn.wthee.pcrtool.ui.detail.equipment

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentAttrAdapter
import cn.wthee.pcrtool.adapters.EquipmentMaterialAdapter
import cn.wthee.pcrtool.data.model.EquipmentData
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

    private lateinit var equip: EquipmentData
    private var isDialog: Boolean = false
    private lateinit var binding: FragmentEquipmentDetailsBinding
    private lateinit var materialAdapter: EquipmentMaterialAdapter
    private lateinit var cusToolbar: ToolbarUtil

    companion object {
        fun getInstance(equip: EquipmentData, isDialog: Boolean) =
            EquipmentDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EQUIP, equip)
                    putBoolean(DIALOG, isDialog)
                }
            }
    }

    private val viewModel = InjectorUtil.provideEquipmentDetailsViewModelFactory()
        .create(EquipmentDetailsViewModel::class.java)

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

    //返回监听
    override fun onResume() {
        super.onResume()
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener(View.OnKeyListener { view, i, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK) {
                goBack()
                return@OnKeyListener true
            }
            false
        })
    }

    private fun init() {
        binding.apply {
            //toolbar
            cusToolbar = ToolbarUtil(toolbar)
            cusToolbar.apply {
                setLeftIcon(R.drawable.ic_back)
                hideRightIcon()
                setTitleColor(R.color.colorPrimary)
                setBackground(R.color.colorBg)
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
            materialAdapter = EquipmentMaterialAdapter()
            binding.material.layoutManager =
                GridLayoutManager(requireContext(), if (it.size == 1) 1 else 2).apply {
                    orientation = LinearLayoutManager.VERTICAL
                }
            binding.material.adapter = materialAdapter
            materialAdapter.submitList(it)
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