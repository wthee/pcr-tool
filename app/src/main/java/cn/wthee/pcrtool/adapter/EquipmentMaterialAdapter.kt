package cn.wthee.pcrtool.adapter

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
import cn.wthee.pcrtool.ui.tool.equip.EquipmentDetailsDialogFragment
import cn.wthee.pcrtool.ui.tool.equip.EquipmentViewModel
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ResourcesUtil
import coil.load
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 装备合成材料列表适配器
 *
 * 列表项布局 [ItemCommonBinding]
 *
 * 列表项数据 [EquipmentMaterial]
 */
class EquipmentMaterialAdapter(
    private val parentBinding: FragmentEquipmentDetailsBinding? = null,
    private val viewModel: EquipmentViewModel
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
                //加载数据
                name.text = MyApplication.context.getString(R.string.equip_count, info.count)
                //装备图片
                pic.load(Constants.EQUIPMENT_URL + info.id + Constants.WEBP) {
                    error(R.drawable.unknown_gray)
                    placeholder(R.drawable.unknown_gray)
                }

                parentBinding?.let { pb ->
                    //修改宽度
                    val params = binding.root.layoutParams as RecyclerView.LayoutParams
                    params.width = RecyclerView.LayoutParams.WRAP_CONTENT
                    binding.root.layoutParams = params
                    //点击查看掉落地区
                    binding.root.setOnClickListener {
                        EquipmentDetailsDialogFragment.materialClickPosition =
                            absoluteAdapterPosition
                        notifyDataSetChanged()
                        pb.progressBar.visibility = View.VISIBLE
                        //掉落地区
                        MainScope().launch {
                            //显示当前查看掉落的装备名称
                            pb.materialName.visibility = View.VISIBLE
                            pb.materialTip.visibility = View.VISIBLE
                            pb.materialName.text = info.name
                            //掉落列表
                            val data = viewModel.getDropInfos(info.id)
                            val adapter = EquipmentDropAdapter()
                            pb.equipDrops.adapter = adapter
                            //动态限制只有一个列表可滚动
                            pb.equipDrops.isNestedScrollingEnabled = true
                            pb.material.isNestedScrollingEnabled = false
                            adapter.submitList(data) {
                                pb.progressBar.visibility = View.GONE
                            }
                            pb.equipDrops.setItemViewCacheSize(50)
                        }
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