package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EventData
import cn.wthee.pcrtool.databinding.ItemEventBinding


class EventHistoryAdapter :
    ListAdapter<EventData, EventHistoryAdapter.ViewHolder>(EventDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(event: EventData) {
            //设置数据
            binding.apply {
                root.animation =
                    AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_list_item)
                //卡池名
                eventName.text = event.title
                //起止日期
                eventDate.text = event.start_time.subSequence(0, 10)
            }
        }
    }

}

private class EventDiffCallback : DiffUtil.ItemCallback<EventData>() {

    override fun areItemsTheSame(
        oldItem: EventData,
        newItem: EventData
    ): Boolean {
        return oldItem.start_time == newItem.start_time
    }

    override fun areContentsTheSame(
        oldItem: EventData,
        newItem: EventData
    ): Boolean {
        return oldItem == newItem
    }
}