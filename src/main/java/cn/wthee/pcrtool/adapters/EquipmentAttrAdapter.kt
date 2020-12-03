package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.data.model.int
import cn.wthee.pcrtool.databinding.ItemEquipmentAttrBinding


class EquipmentAttrAdapter :
    ListAdapter<AttrValue, EquipmentAttrAdapter.ViewHolder>(AttrDiffCallback()) {
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
        fun bind(data: AttrValue) {
            binding.apply {
                this.value.animation =
                    AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_scale)
                titleAttr.text = data.title
                this.value.text = data.value.int.toString()
            }
        }
    }

}

private class AttrDiffCallback : DiffUtil.ItemCallback<AttrValue>() {

    override fun areItemsTheSame(
        oldItem: AttrValue,
        newItem: AttrValue
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: AttrValue,
        newItem: AttrValue
    ): Boolean {
        return oldItem == newItem
    }
}