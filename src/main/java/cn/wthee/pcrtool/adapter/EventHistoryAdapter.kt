package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EventData
import cn.wthee.pcrtool.databinding.ItemEventBinding
import cn.wthee.pcrtool.utils.ResourcesUtil.setTitleBackground
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 活动记录列表适配器
 *
 * 列表项布局 [ItemEventBinding]
 *
 * 列表项数据 [EventData]
 */
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
                //内容
                subTitle.text = event.title
                //角色碎片
                val adapter = IconListAdapter()
                icons.adapter = adapter
                adapter.submitList(event.unitIds.intArrayList())
                val startDate = event.startTime.subSequence(0, 10).toString()
                val endDate = event.endTime.subSequence(0, 10).toString()
                when {
                    //支线
                    event.eventId / 10000 == 2 -> {
                        type.text = "支线"
                        type.setTitleBackground(R.color.cool_apk)
                        title.text = startDate
                        days.visibility = View.GONE
                    }
                    //复刻
                    event.eventId / 10000 == 1 && event.storyId % 1000 != event.eventId % 1000 -> {
                        type.text = "复刻"
                        type.setTitleBackground(R.color.news_system)
                        title.text = "$startDate ~ $endDate"
                        days.text = "${endDate.days(startDate)} 天"
                    }
                    //正常
                    else -> {
                        type.text = "活动"
                        type.setTitleBackground(R.color.news_update)
                        val day = endDate.days(startDate)
                        if (day == "00") days.visibility = View.GONE
                        title.text = "$startDate ~ $endDate"
                        days.text = "${endDate.days(startDate)} 天"
                    }
                }
            }
        }
    }

}

private class EventDiffCallback : DiffUtil.ItemCallback<EventData>() {

    override fun areItemsTheSame(
        oldItem: EventData,
        newItem: EventData
    ): Boolean {
        return oldItem.eventId == newItem.eventId
    }

    override fun areContentsTheSame(
        oldItem: EventData,
        newItem: EventData
    ): Boolean {
        return oldItem == newItem
    }
}