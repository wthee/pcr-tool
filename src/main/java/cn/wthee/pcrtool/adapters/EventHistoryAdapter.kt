package cn.wthee.pcrtool.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.entity.EventStoryData
import cn.wthee.pcrtool.databinding.ItemEventDataBinding


class EventHistoryAdapter :
    ListAdapter<EventStoryData, EventHistoryAdapter.ViewHolder>(EventDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEventDataBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEventDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(event: EventStoryData) {
            //设置数据
            binding.apply {
                root.animation =
                    AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_translate_y)
                //卡池名
                eventName.text = event.title
                //起止日期
                eventDate.text = event.start_time.subSequence(0, 10)
            }
        }
    }

}

private class EventDiffCallback : DiffUtil.ItemCallback<EventStoryData>() {

    override fun areItemsTheSame(
        oldItem: EventStoryData,
        newItem: EventStoryData
    ): Boolean {
        return oldItem.story_group_id == newItem.story_group_id
    }

    override fun areContentsTheSame(
        oldItem: EventStoryData,
        newItem: EventStoryData
    ): Boolean {
        return oldItem == newItem
    }
}