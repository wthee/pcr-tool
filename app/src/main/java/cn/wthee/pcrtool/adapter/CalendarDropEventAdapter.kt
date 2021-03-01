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
import cn.wthee.pcrtool.data.view.DropEvent
import cn.wthee.pcrtool.databinding.ItemCalendarEventJpBinding
import cn.wthee.pcrtool.utils.ResourcesUtil

/**
 * 日历掉落加倍事项列表适配器
 *
 * 列表项布局：[ItemCalendarEventJpBinding]
 *
 * 列表项数据：[DropEvent]
 */
class CalendarDropEventAdapter :
    ListAdapter<DropEvent, CalendarDropEventAdapter.ViewHolder>(CalendarDropDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCalendarEventJpBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCalendarEventJpBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: DropEvent) {
            binding.apply {
                root.animation =
                    AnimationUtils.loadAnimation(
                        MyApplication.context,
                        R.anim.anim_scale
                    )
                val title = getType(item.type, item.getFixedValue())
                calendarEventTitle.text = title
                if (title.isNotEmpty()) {
                    date.visibility = View.VISIBLE
                    date.text =
                        item.getFixedStartTime().substring(5, 10) + " 至 " + item.getFixedEndTime()
                            .substring(5, 10)
                }
            }
        }

        private fun getType(type: String, value: String): String {
            var str = ""
            val list = type.split("-")
            list.forEachIndexed { index, s ->
                val title = when (s.toInt()) {
                    31 -> ResourcesUtil.getString(R.string.normal)
                    32 -> ResourcesUtil.getString(R.string.hard)
                    39 -> ResourcesUtil.getString(R.string.very_hard)
                    34 -> ResourcesUtil.getString(R.string.explore)
                    37 -> ResourcesUtil.getString(R.string.shrine)
                    38 -> ResourcesUtil.getString(R.string.temple)
                    45 -> ResourcesUtil.getString(R.string.dungeon)
                    else -> ""
                }
                str += if (index < list.size - 1 && title != "") {
                    (title + "\n")
                } else {
                    title
                }
            }
            return str.replace("x", value)
        }

    }

}

private class CalendarDropDiffCallback : DiffUtil.ItemCallback<DropEvent>() {

    override fun areItemsTheSame(
        oldItem: DropEvent,
        newItem: DropEvent
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: DropEvent,
        newItem: DropEvent
    ): Boolean {
        return oldItem == newItem
    }
}