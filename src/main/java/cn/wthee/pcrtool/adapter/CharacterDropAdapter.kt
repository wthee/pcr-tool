package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ItemDropInfo
import cn.wthee.pcrtool.databinding.ItemCharacterDropBinding
import cn.wthee.pcrtool.utils.ResourcesUtil

/**
 * 角色碎片掉落列表适配器
 *
 * 列表项布局 [ItemCharacterDropBinding]
 *
 * 列表项数据 [ItemDropInfo]
 */
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
                val pre = when (info.quest_id / 1000000) {
                    11 -> "N"
                    12 -> "H"
                    13 -> "VH"
                    else -> ""
                }
                questNum.text =
                    MyApplication.context.getString(R.string.quest_name, pre, info.getNum())
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