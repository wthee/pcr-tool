package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.EquipmentMaterial
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsBinding
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsDialogFragment
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsViewModel
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ResourcesUtil
import coil.load
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class EquipmentMaterialAdapter(
    private val partentBinding: FragmentEquipmentDetailsBinding,
    private val viewModel: EquipmentDetailsViewModel
) :
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
                //修改宽度
                val params = root.layoutParams as RecyclerView.LayoutParams
                params.width = RecyclerView.LayoutParams.WRAP_CONTENT
                root.layoutParams = params
                //加载数据
                val subName = if (info.name.length > 8) info.name.substring(0, 7) else
                    info.name
                name.text =
                    MyApplication.context.getString(R.string.equip_count, subName, info.count)
                pic.load(Constants.EQUIPMENT_URL + info.id + Constants.WEBP) {
                    error(R.drawable.unknow_gray)
                    placeholder(R.drawable.unknow_gray)
                }
                //点击查看掉落地区
                root.setOnClickListener {
                    EquipmentDetailsDialogFragment.materialClickPosition = absoluteAdapterPosition
                    notifyDataSetChanged()
                    partentBinding.progressBar.visibility = View.VISIBLE
                    //掉落地区
                    MainScope().launch {
                        //显示当前查看掉落的装备名称
                        partentBinding.materialName.visibility = View.VISIBLE
                        partentBinding.materialTip.visibility = View.VISIBLE
                        partentBinding.materialName.text = info.name
                        //掉落列表
                        val data = viewModel.getDropInfos(info.id)
                        val adapter = EquipmentDropAdapter()
                        partentBinding.drops.adapter = adapter
                        //动态限制只有一个列表可滚动
                        partentBinding.drops.isNestedScrollingEnabled = true
                        partentBinding.material.isNestedScrollingEnabled = false
                        adapter.submitList(data) {
                            partentBinding.progressBar.visibility = View.INVISIBLE
                        }
                        partentBinding.drops.setItemViewCacheSize(50)
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