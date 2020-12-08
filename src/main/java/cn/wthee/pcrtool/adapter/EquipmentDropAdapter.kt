package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentDropInfo
import cn.wthee.pcrtool.databinding.ItemEquipmentDropBinding
import cn.wthee.pcrtool.utils.ResourcesUtil


class EquipmentDropAdapter :
    ListAdapter<EquipmentDropInfo, EquipmentDropAdapter.ViewHolder>(DropDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEquipmentDropBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemEquipmentDropBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: EquipmentDropInfo) {
            binding.apply {
                quest.text = info.getName()
                //地图难度
                val pre = when (info.questId / 1000000) {
                    11 -> "N"
                    12 -> "H"
                    13 -> "VH"
                    else -> ""
                }
                questNum.text =
                    MyApplication.context.getString(R.string.quest_name, pre, info.getNum())
                //颜色
                val color = when (info.questId / 1000000) {
                    11 -> R.color.color_map_n
                    12 -> R.color.color_map_h
                    13 -> R.color.color_map_vh
                    else -> R.color.color_map_n
                }
                questNum.setTextColor(ResourcesUtil.getColor(color))
                val adapter = EquipmentDropDetailAdapter(info.eid)
                drops.adapter = adapter
                adapter.submitList(info.getAllOdd())
                drops.setItemViewCacheSize(20)
            }
        }
    }

}

private class DropDiffCallback : DiffUtil.ItemCallback<EquipmentDropInfo>() {

    override fun areItemsTheSame(
        oldItem: EquipmentDropInfo,
        newItem: EquipmentDropInfo
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: EquipmentDropInfo,
        newItem: EquipmentDropInfo
    ): Boolean {
        return oldItem == newItem
    }
}