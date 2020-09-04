package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.model.CharacterAttr
import cn.wthee.pcrtool.databinding.ItemCharacterAttrBinding
import kotlin.math.round


class CharacterAttrAdapter :
    ListAdapter<CharacterAttr, CharacterAttrAdapter.ViewHolder>(CharacterAttrDiffCallback()) {
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
        fun bind(data: CharacterAttr) {
            binding.apply {
                titleAttr.text = data.type
                value.text = round(data.value).toInt().toString()
            }
        }
    }

}

private class CharacterAttrDiffCallback : DiffUtil.ItemCallback<CharacterAttr>() {

    override fun areItemsTheSame(
        oldItem: CharacterAttr,
        newItem: CharacterAttr
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CharacterAttr,
        newItem: CharacterAttr
    ): Boolean {
        return oldItem == newItem
    }
}