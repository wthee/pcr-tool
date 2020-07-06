package cn.wthee.pcrtool.ui.detail.equipment

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.setMargins
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.transition.TransitionInflater
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentAttrAdapter
import cn.wthee.pcrtool.adapters.EquipmentMaterialAdapter
import cn.wthee.pcrtool.data.model.EquipmentData
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsBinding
import cn.wthee.pcrtool.utils.*
import javax.inject.Singleton


private const val EQUIP = "equip"
private const val DIALOG = "dialog"

@Singleton
class EquipmentDetailsFragment : Fragment() {

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
        arguments?.let {
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
        setHasOptionsMenu(true)
        init()
        setObserve()
        viewModel.getEquipInfos(equip)
        //从角色页面打开时
        if (isDialog) {
            //隐藏装备属性
            binding.attrs.visibility = View.GONE
            //取消margin
            val params = binding.layoutCard.layoutParams as FrameLayout.LayoutParams
            params.setMargins(0)
            binding.layoutCard.layoutParams = params
            binding.layoutMain.setBackgroundColor(Color.TRANSPARENT)
            //滑动
            DrawerUtil.bindAllViewOnTouchListener(
                binding.root,
                parentFragment as DialogFragment,
                arrayListOf(binding.layoutContent)
            )
        }
        return binding.root
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
            cusToolbar.setLeftIcon(R.drawable.ic_detail_back)
            cusToolbar.hideRightIcon()
            cusToolbar.setTitleColor(R.color.colorPrimary)
            cusToolbar.setBackground(R.color.colorWhite)
            cusToolbar.setTitleCenter()
            cusToolbar.leftIcon.setOnClickListener {
                goBack()
            }
            cusToolbar.title.text = equip.equipmentName
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
            //合成素材
            materialAdapter = EquipmentMaterialAdapter()
            PagerSnapHelper().attachToRecyclerView(material)
            material.adapter = materialAdapter
        }
    }

    private fun setObserve() {
        viewModel.equipMaterialInfos.observe(viewLifecycleOwner, Observer {
            binding.titleMaterial.text = resources.getString(R.string.material, it.size)
            materialAdapter.submitList(it)
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.equipProgressBar.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun goBack(){
        if(isDialog){
            (parentFragment as DialogFragment).dismiss()
        }else{
            view?.findNavController()?.navigateUp()
        }
    }

}