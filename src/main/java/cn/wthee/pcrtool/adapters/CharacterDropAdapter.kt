package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.view.ItemDropInfo
import cn.wthee.pcrtool.databinding.ItemCharacterDropBinding
import cn.wthee.pcrtool.utils.ResourcesUtil


class CharacterDropAdapter :
    ListAdapter<ItemDropInfo, CharacterDropAdapter.ViewHolder>(CharacterDropDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCharacterDropBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemCharacterDropBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: ItemDropInfo) {
            binding.apply {
                quest.text = info.getName()
                //地图难度
                questNum.text = when (info.quest_id / 1000000) {
                    11 -> "N"
                    12 -> "H"
                    13 -> "VH"
                    else -> ""
                } + "-" + info.getNum()
                //颜色
                val color = when (info.quest_id / 1000000) {
                    11 -> R.color.color_map_n
                    12 -> R.color.color_map_h
                    13 -> R.color.color_map_vh
                    else -> R.color.color_map_n
                }
                questNum.setTextColor(ResourcesUtil.getColor(color))
            }
        }
    }

}

private class CharacterDropDiffCallback : DiffUtil.ItemCallback<ItemDropInfo>() {

    override fun areItemsTheSame(
        oldItem: ItemDropInfo,
        newItem: ItemDropInfo
    ): Boolean {
        return oldItem.quest_id == newItem.quest_id
    }

    override fun areContentsTheSame(
        oldItem: ItemDropInfo,
        newItem: ItemDropInfo
    ): Boolean {
        return oldItem == newItem
    }
}