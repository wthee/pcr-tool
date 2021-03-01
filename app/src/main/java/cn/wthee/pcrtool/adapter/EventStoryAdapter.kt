package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.entity.EventStoryDetail
import cn.wthee.pcrtool.databinding.ItemEventStoryBinding

/**
 * 活动剧情故事列表适配器
 *
 * 列表项布局 [ItemEventStoryBinding]
 *
 * 列表项数据 [EventStoryDetail]
 */
class EventStoryAdapter(private val storyName: String) :
    ListAdapter<EventStoryDetail, EventStoryAdapter.ViewHolder>(EventStoryDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEventStoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEventStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventStoryDetail) {
            //设置数据
            binding.apply {
                title.text = event.title
                subTitle.text = event.sub_title
            }
        }
    }

}

private class EventStoryDiffCallback : DiffUtil.ItemCallback<EventStoryDetail>() {

    override fun areItemsTheSame(
        oldItem: EventStoryDetail,
        newItem: EventStoryDetail
    ): Boolean {
        return oldItem.story_id == newItem.story_id
    }

    override fun areContentsTheSame(
        oldItem: EventStoryDetail,
        newItem: EventStoryDetail
    ): Boolean {
        return oldItem == newItem
    }
}