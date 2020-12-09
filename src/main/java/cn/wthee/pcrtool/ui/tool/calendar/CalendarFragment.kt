package cn.wthee.pcrtool.ui.tool.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CalendarEventAdapter
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.network.model.CalendarDay
import cn.wthee.pcrtool.databinding.FragmentToolCalendarBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日历
 */
class CalendarFragment : Fragment() {


    private lateinit var binding: FragmentToolCalendarBinding
    private lateinit var adapter: CalendarEventAdapter
    private var events = listOf<CalendarDay>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolCalendarBinding.inflate(inflater, container, false)
        //列表
        adapter = CalendarEventAdapter()
        binding.events.adapter = adapter
        //选择监听显示数据
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            showDayEvents(year, month + 1, dayOfMonth)
            binding.currentDate.text = "${month + 1} 月 $dayOfMonth 日"
            binding.events.smoothScrollToPosition(0)
        }
        //默认
        val cal = Calendar.getInstance()
        cal.time = Date(System.currentTimeMillis())
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        binding.currentDate.text = "${month} 月 $day 日"
        binding.calendarView.minDate =
            SimpleDateFormat("yyyy/MM/dd")
                .parse("2020/06/06")
                .time
        binding.calendarView.maxDate =
            SimpleDateFormat("yyyy/MM/dd")
                .parse("$year/$month/${cal.getActualMaximum(Calendar.DATE)}")
                .time
        MainScope().launch {
            getMonthEvents(object : OnLoadFinish {
                override fun finish(maxDate: String) {
                    binding.calendarView.maxDate =
                        SimpleDateFormat("yyyy/MM/dd")
                            .parse(maxDate)
                            .time
                    showDayEvents(year, month, day)
                }
            })
        }

        //设置头部
        ToolbarUtil(binding.toolCalendar).setToolHead(
            R.drawable.ic_calender,
            getString(R.string.tool_calendar)
        )

        return binding.root

    }

    //一次获取当月全部的
    private suspend fun getMonthEvents(onLoadFinish: OnLoadFinish) {
        val list = MyAPIRepository.getCalendar()
        if (list.status == 0) {
            events = list.data!!.days
            onLoadFinish.finish(list.data!!.maxDate)
        } else if (list.status == -1) {
            ToastUtil.short(list.message)
        }
    }

    private fun showDayEvents(year: Int, month: Int, dayOfMonth: Int) {
        val eventData = events.filter {
            it.date.split("/")[0].toInt() == year
                    && it.date.split("/")[1].toInt() == month
                    && it.date.split("/")[2].toInt() == dayOfMonth

        }
        adapter.addHeaderAndSubmitList(eventData)
        binding.loading.root.visibility = View.GONE
    }

    interface OnLoadFinish {
        fun finish(maxDate: String)
    }

}