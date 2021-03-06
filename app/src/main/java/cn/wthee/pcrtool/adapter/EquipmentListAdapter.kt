package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentManager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.EquipmentMaxData
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.ui.tool.equip.EquipmentDetailsDialogFragment
import cn.wthee.pcrtool.ui.tool.equip.EquipmentListFragment
import cn.wthee.pcrtool.utils.Constants.EQUIPMENT_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.ResourcesUtil
import coil.load

/**
 * 装备列表适配器
 *
 * 列表项布局 [ItemCommonBinding]
 *
 * 列表项数据 [EquipmentMaxData]
 */
class EquipmentListAdapter(
    private val fragmentManager: FragmentManager
) : PagingDataAdapter<EquipmentMaxData, EquipmentListAdapter.ViewHolder>(EquipDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCommonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(equip: EquipmentMaxData?) {
            if (equip != null) {
                //设置数据
                binding.apply {
                    val ctx = MyApplication.context
                    root.animation = AnimationUtils.loadAnimation(ctx, R.anim.anim_list_item)
                    //是否收藏
                    val isLoved =
                        EquipmentListFragment.equipFilterParams.starIds.contains(equip.equipmentId)
                    //装备名称
                    name.setTextColor(ResourcesUtil.getColor(if (isLoved) R.color.colorPrimary else R.color.text))
                    name.text = equip.equipmentName
                    //加载装备图片
                    val picUrl = EQUIPMENT_URL + equip.equipmentId + WEBP
                    pic.load(picUrl) {
                        error(R.drawable.unknown_gray)
                        placeholder(R.drawable.unknown_gray)
                    }
                    //设置点击跳转
                    root.setOnClickListener {
                        EquipmentDetailsDialogFragment.getInstance(equip)
                            .show(fragmentManager, "equip_details")
                    }
                    //长按事件
                    binding.root.setOnLongClickListener {
                        EquipmentListFragment.equipFilterParams.addOrRemove(equip.equipmentId)
                        EquipmentListFragment.list.adapter?.notifyItemChanged(
                            absoluteAdapterPosition
                        )
                        return@setOnLongClickListener true
                    }
                }
            }
        }
    }
}

class EquipDiffCallback : DiffUtil.ItemCallback<EquipmentMaxData>() {

    override fun areItemsTheSame(
        oldItem: EquipmentMaxData,
        newItem: EquipmentMaxData
    ): Boolean {
        return oldItem.equipmentId == newItem.equipmentId
    }

    override fun areContentsTheSame(
        oldItem: EquipmentMaxData,
        newItem: EquipmentMaxData
    ): Boolean {
        return oldItem == newItem
    }
}