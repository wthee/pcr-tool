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
import cn.wthee.pcrtool.data.model.CalendarData
import cn.wthee.pcrtool.databinding.ItemCalendarEventBinding


class CalendarEventAdapter :
    ListAdapter<CalendarData, CalendarEventAdapter.ViewHolder>(CalendarDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCalendarEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCalendarEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: CalendarData) {
            binding.apply {
                root.animation =
                    AnimationUtils.loadAnimation(
                        MyApplication.context,
                        R.anim.anim_translate_y
                    )
                calendarEventTitle.text = data.title
                date.text = data.startDate + "~" + data.endDate

            }
        }

    }

}

private class CalendarDiffCallback : DiffUtil.ItemCallback<CalendarData>() {

    override fun areItemsTheSame(
        oldItem: CalendarData,
        newItem: CalendarData
    ): Boolean {
        return oldItem.date == newItem.date && oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: CalendarData,
        newItem: CalendarData
    ): Boolean {
        return oldItem == newItem
    }
}