package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.EventData
import cn.wthee.pcrtool.databinding.ItemEventBinding
import cn.wthee.pcrtool.ui.tool.event.EventStoryDetailsDialogFragment
import cn.wthee.pcrtool.utils.ResourcesUtil.setTitleBackground
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 活动记录列表适配器
 *
 * 列表项布局 [ItemEventBinding]
 *
 * 列表项数据 [EventData]
 */
class EventHistoryAdapter(
    private val fragmentManager: FragmentManager
) :
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
                subTitle.text = event.title.deleteSpace()
                //角色碎片
                val adapter = IconListAdapter()
                icons.adapter = adapter
                adapter.submitList(event.unitIds.intArrayList())
                val startDate = event.startTime.substring(0, 10)
                val endDate = event.endTime.substring(0, 10)
                if (startDate == "2030/12/30") {
                    title.text = "活动预告"
                } else {
                    title.text = "$startDate ~ $endDate"
                }
                val day = endDate.days(startDate)
                if (day == "00") {
                    days.visibility = View.GONE
                } else {
                    days.text = "$day 天"
                }
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
                    }
                    //正常
                    else -> {
                        type.text = "活动"
                        type.setTitleBackground(R.color.news_update)
                    }
                }
                //点击查看剧情列表
                root.setOnClickListener {
                    EventStoryDetailsDialogFragment.getInstance(
                        event.storyId,
                        event.title.deleteSpace()
                    )
                        .show(fragmentManager, "story_detail")
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