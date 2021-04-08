package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.EquipmentMaterial
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.ui.tool.equip.EquipmentDetailsDialogFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ResourcesUtil
import coil.load
import com.google.android.material.textview.MaterialTextView

/**
 * 装备合成材料列表适配器
 *
 * 列表项布局 [ItemCommonBinding]
 *
 * 列表项数据 [EquipmentMaterial]
 */
class EquipmentMaterialAdapter(private val callback: CallBack? = null) :
    ListAdapter<EquipmentMaterial, EquipmentMaterialAdapter.ViewHolder>(MaterialDiffCallback()) {
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
        holder.apply {
            bind(getItem(position))
            itemView.findViewById<MaterialTextView>(R.id.name)
                .setTextColor(
                    ResourcesUtil.getColor(
                        if (position == EquipmentDetailsDialogFragment.materialClickPosition)
                            R.color.red
                        else
                            R.color.text
                    )
                )
        }
    }

    inner class ViewHolder(private val binding: ItemCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: EquipmentMaterial) {
            binding.apply {
                //加载数据
                name.text = root.context.getString(R.string.equip_count, info.count)
                //装备图片
                pic.load(Constants.EQUIPMENT_URL + info.id + Constants.WEBP) {
                    error(R.drawable.unknown_gray)
                    placeholder(R.drawable.unknown_gray)
                }

                callback?.let { pb ->
                    //修改宽度
                    val params = binding.root.layoutParams as RecyclerView.LayoutParams
                    params.width = RecyclerView.LayoutParams.WRAP_CONTENT
                    binding.root.layoutParams = params
                    //点击查看掉落地区
                    binding.root.setOnClickListener {
                        EquipmentDetailsDialogFragment.materialClickPosition =
                            absoluteAdapterPosition
                        notifyDataSetChanged()
                        callback.todo(info)
                    }
                }

            }
        }
    }

}

private class MaterialDiffCallback : DiffUtil.ItemCallback<EquipmentMaterial>() {

    override fun areItemsTheSame(
        oldItem: EquipmentMaterial,
        newItem: EquipmentMaterial
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: EquipmentMaterial,
        newItem: EquipmentMaterial
    ): Boolean {
        return oldItem == newItem
    }
}