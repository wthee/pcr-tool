package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.model.AttrData
import cn.wthee.pcrtool.databinding.ItemCharacterAttrBinding


class CharacterAttrAdapter :
    ListAdapter<AttrData, CharacterAttrAdapter.ViewHolder>(CharacterAttrDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCharacterAttrBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemCharacterAttrBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AttrData) {
            binding.apply {
                titleAttr.text = data.title
                value.text = data.getIntValue().toString()
            }
        }
    }

}

private class CharacterAttrDiffCallback : DiffUtil.ItemCallback<AttrData>() {

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