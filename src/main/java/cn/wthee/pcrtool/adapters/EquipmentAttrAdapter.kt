package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.AttrData
import cn.wthee.pcrtool.databinding.ItemEquipmentAttrBinding


class EquipmentAttrAdapter :
    ListAdapter<AttrData, EquipmentAttrAdapter.ViewHolder>(AttrDiffCallback()) {
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
        fun bind(data: AttrData) {
            binding.apply {
                value.animation =
                    AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.anim_scale)
                titleAttr.text = data.title
                value.text = data.getIntValue().toString()
            }
        }
    }

}

private class AttrDiffCallback : DiffUtil.ItemCallback<AttrData>() {

    override fun areItemsTheSame(
        oldItem: AttrData,
        newItem: AttrData
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: AttrData,
        newItem: AttrData
    ): Boolean {
        return oldItem == newItem
    }
}