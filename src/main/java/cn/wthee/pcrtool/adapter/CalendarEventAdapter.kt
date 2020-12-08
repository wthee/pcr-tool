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
import cn.wthee.pcrtool.data.network.model.CalendarData
import cn.wthee.pcrtool.data.network.model.CalendarDataItem
import cn.wthee.pcrtool.data.network.model.CalendarEvent
import cn.wthee.pcrtool.databinding.ItemCalendarEventBinding
import cn.wthee.pcrtool.databinding.ItemCalendarEventHeaderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CalendarEventAdapter :
    ListAdapter<CalendarDataItem, CalendarEventAdapter.ViewHolder>(CalendarDiffCallback()) {

    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<CalendarData>) {
        adapterScope.launch {
            val items = when {
                list.isEmpty() -> listOf(CalendarDataItem.Header("无活动")) + listOf(
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
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
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