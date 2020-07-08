package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EquipmentAttr
import cn.wthee.pcrtool.databinding.ItemEquipmentAttrBinding


class EquipmentAttrAdapter :
    ListAdapter<EquipmentAttr, EquipmentAttrAdapter.ViewHolder>(AttrDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEquipmentAttrBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemEquipmentAttrBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attrs: EquipmentAttr) {
            binding.apply {
                value.animation =
                    AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.anim_scale)
                titleAttr.text = attrs.title
                value.text = attrs.value.toString()
            }
        }
    }

}

private class AttrDiffCallback : DiffUtil.ItemCallback<EquipmentAttr>() {

    override fun areItemsTheSame(
        oldItem: EquipmentAttr,
        newItem: EquipmentAttr
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: EquipmentAttr,
        newItem: EquipmentAttr
    ): Boolean {
        return oldItem == newItem
    }
}