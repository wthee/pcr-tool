package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.network.model.CalendarContent
import cn.wthee.pcrtool.data.network.model.CalendarDataItem
import cn.wthee.pcrtool.data.network.model.CalendarEvent
import cn.wthee.pcrtool.databinding.ItemCalendarEventBinding
import cn.wthee.pcrtool.databinding.ItemCalendarEventHeaderBinding

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

/**
 * 日历事项列表适配器
 *
 * 列表项布局：分组名 [ItemCalendarEventHeaderBinding]，分组内容 [ItemCalendarEventBinding]
 *
 * 列表项数据：分组名 [CalendarDataItem.Header]，分组内容 [CalendarDataItem.Item]
 */
class CalendarEventAdapter :
    ListAdapter<CalendarDataItem, CalendarEventAdapter.ViewHolder>(CalendarDiffCallback()) {

    /**
     * 处理事项 [list] 为 [CalendarDataItem] 数组，并更新列表
     */
    fun addHeaderAndSubmitList(list: List<CalendarContent>?) {
        val items = when {
            list == null || list.isEmpty() -> listOf(CalendarDataItem.Header("无活动")) + listOf(
                CalendarDataItem.Item(
                    CalendarEvent("", "", "暂无")
                )
            )
            else -> {
                val datas = arrayListOf<CalendarDataItem>()
                list.forEach {
                    datas.add(CalendarDataItem.Header(it.type))
                    it.events.forEach { event ->
                        datas.add(CalendarDataItem.Item(event))
                    }
                }
                datas
            }
        }
        submitList(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> ViewHolder(
                ItemCalendarEventHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ITEM_VIEW_TYPE_ITEM -> ViewHolder(
                ItemCalendarEventBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: CalendarDataItem) {
            when (item) {
                is CalendarDataItem.Item -> {
                    (binding as ItemCalendarEventBinding).apply {
                        root.animation =
                            AnimationUtils.loadAnimation(
                                MyApplication.context,
                                R.anim.anim_scale
                            )
                        calendarEventTitle.text = item.data.title
                        if (item.data.endDate != "") {
                            date.text = item.data.startDate + " 至 " + item.data.endDate
                        } else {
                            date.text = ""
                        }
                    }
                }
                is CalendarDataItem.Header -> {
                    (binding as ItemCalendarEventHeaderBinding).apply {
                        calendarType.text = getType(item.title)
                    }
                }
            }
        }

        private fun getType(type: String): String {
            return when (type) {
                "qdhd" -> "庆典活动"
                "tdz" -> "团队战"
                "tbhd" -> "特别活动"
                "jqhd" -> "剧情活动"
                "jssr" -> "角色生日"
                else -> "今日活动"
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CalendarDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is CalendarDataItem.Item -> ITEM_VIEW_TYPE_ITEM
        }
    }
}

private class CalendarDiffCallback : DiffUtil.ItemCallback<CalendarDataItem>() {

    override fun areItemsTheSame(
        oldItem: CalendarDataItem,
        newItem: CalendarDataItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CalendarDataItem,
        newItem: CalendarDataItem
    ): Boolean {
        return oldItem == newItem
    }
}