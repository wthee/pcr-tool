package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EquipmentDropInfo
import cn.wthee.pcrtool.databinding.ItemEquipmentDropBinding


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
                binding.root.animation =
                    AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.item_equip_drop)
                quest.text = info.getName()
                questNum.text = info.getNum()
                odd.text = info.odds.find { it.eid == info.eid }?.odd.toString() + "%"
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