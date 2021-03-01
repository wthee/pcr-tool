package cn.wthee.pcrtool.ui.tool.equip

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.EquipmentAttrAdapter
import cn.wthee.pcrtool.adapter.EquipmentMaterialAdapter
import cn.wthee.pcrtool.data.view.EquipmentMaxData
import cn.wthee.pcrtool.data.view.allNotZero
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsBinding
import cn.wthee.pcrtool.ui.common.CommonBottomSheetDialogFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ResourcesUtil
import coil.load


private const val EQUIP = "equip"

/**
 * 装备详情页面
 *
 * 根据 [equip] 显示装备信息
 *
 * 页面布局 [FragmentEquipmentDetailsBinding]
 *
 * ViewModels [EquipmentViewModel]
 */
class EquipmentDetailsDialogFragment : CommonBottomSheetDialogFragment() {

    companion object {
        var isLoved = false
        fun getInstance(equip: EquipmentMaxData) =
            EquipmentDetailsDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EQUIP, equip)
                }
            }

        var materialClickPosition = -1
    }

    private lateinit var equip: EquipmentMaxData
    private lateinit var binding: FragmentEquipmentDetailsBinding
    private lateinit var materialAdapter: EquipmentMaterialAdapter

    private val viewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            equip = it.getSerializable(EQUIP) as EquipmentMaxData
        }
        isLoved = EquipmentListFragment.equipFilterParams.starIds.contains(equip.equipmentId)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEquipmentDetailsBinding.inflate(inflater, container, false)
        init()
        setObserve()
        setLove(isLoved)
        viewModel.getEquipInfos(equip)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        materialClickPosition = -1
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.apply {
            detail.apply {
                //图标
                val picUrl = Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEBP
                itemPic.load(picUrl) {
                    error(R.drawable.unknown_gray)
                }
                //名称
                equipName.text = equip.equipmentName
                //描述
                desc.text = equip.getDesc()
                //属性词条
                val adapter = EquipmentAttrAdapter()
                equipAttrs.adapter = adapter
                adapter.submitList(equip.attr.allNotZero())
            }
            //动态限制只有一个列表可滚动
//            material.setOnTouchListener { _, _ ->
//                if (!material.isNestedScrollingEnabled) material.isNestedScrollingEnabled = true
//                if (equipDrops.isNestedScrollingEnabled) equipDrops.isNestedScrollingEnabled = false
//                return@setOnTouchListener false
//            }
            //点击收藏
            stared.setOnClickListener {
                isLoved = !isLoved
                EquipmentListFragment.equipFilterParams.addOrRemove(equip.equipmentId)
                setLove(isLoved)
            }
        }
    }

    private fun setObserve() {
        viewModel.equipMaterialInfos.observe(viewLifecycleOwner) {
            //合成素材
            if (it.isNotEmpty()) {
                binding.materialCount.text = getString(R.string.title_material, it.size)
                materialAdapter = EquipmentMaterialAdapter(binding, viewModel)
                binding.material.adapter = materialAdapter
                materialAdapter.submitList(it)
            } else {
                binding.material.visibility = View.GONE
            }
        }
    }

    //设置收藏
    private fun setLove(isLoved: Boolean) {
        binding.detail.equipName.setTextColor(ResourcesUtil.getColor(if (Companion.isLoved) R.color.colorPrimary else R.color.text))
        binding.stared.imageTintList =
            ColorStateList.valueOf(ResourcesUtil.getColor(if (isLoved) R.color.colorPrimary else R.color.alphaPrimary))
        try {
            EquipmentListFragment.pageAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
        }
    }
}