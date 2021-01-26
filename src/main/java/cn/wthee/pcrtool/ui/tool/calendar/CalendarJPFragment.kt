package cn.wthee.pcrtool.ui.tool.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CalendarDropEventAdapter
import cn.wthee.pcrtool.data.db.view.DropEvent
import cn.wthee.pcrtool.databinding.FragmentToolCalendarBinding
import cn.wthee.pcrtool.utils.*
import com.applandeo.materialcalendarview.CalendarUtils.getDrawableText
import com.applandeo.materialcalendarview.EventDay
import java.util.*


/**
 * 日历
 *
 * 页面布局 [FragmentToolCalendarBinding]
 *
 * ViewModels [CalendarViewModel]
 */
class CalendarJPFragment : Fragment() {


    private lateinit var binding: FragmentToolCalendarBinding
    private lateinit var adapter: CalendarDropEventAdapter
    private var events = listOf<DropEvent>()
    private var mYear = 0
    private var mMonth = 0
    private lateinit var minCal: Calendar
    private lateinit var maxCal: Calendar
    private lateinit var cal: Calendar
    private val calendarViewModel by activityViewModels<CalendarViewModel> {
        InjectorUtil.provideCalendarViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolCalendarBinding.inflate(inflater, container, false)
        //初始值
        init()
        //监听
        setListener()
        //设置头部
        ToolbarHelper(binding.toolCalendar).setMainToolbar(
            R.drawable.ic_calendar,
            getString(R.string.tool_calendar)
        )

        return binding.root
    }

    private fun setListener() {
        binding.calendarView.apply {
            setDate(cal)
            //最大日期
            setMaximumDate(cal)
            setOnDayClickListener { eventDay ->
                showDayEvents(eventDay.calendar)
            }
            //月份切换监听
            setOnForwardPageChangeListener {
                mMonth++
                if (mMonth > 12) {
                    mMonth = 1
                    mYear++
                }
                addIcon(mYear, mMonth)
            }
            setOnPreviousPageChangeListener {
                mMonth--
                if (mMonth < 1) {
                    mMonth = 12
                    mYear--
                }
                addIcon(mYear, mMonth)
            }
        }

        //回到今天
        binding.fabToday.setOnClickListener {
            showDayEvents(cal)
        }
    }

    private fun init() {
        binding.fabToday.hide()
        cal = Calendar.getInstance()
        cal.time = Date(System.currentTimeMillis())
        val year = cal.get(Calendar.YEAR)
        mYear = year
        val month = cal.get(Calendar.MONTH) + 1
        mMonth = month
        val day = cal.get(Calendar.DAY_OF_MONTH)
        maxCal = cal
        //默认选中
        binding.currentDate.text =
            resources.getString(R.string.date_m_d, month.toString(), day.toString())
        //列表
        adapter = CalendarDropEventAdapter()
        binding.events.adapter = adapter
        //初始加载
        calendarViewModel.getDropEvent()
        //优化按月份加载
        calendarViewModel.dropEvents.observe(viewLifecycleOwner, { list ->
            if (list.isNotEmpty()) {
                events = list.sortedBy {
                    it.getFixedStartTime()
                }
                binding.calendarView.visibility = View.VISIBLE
                //设置最大值
                maxCal = Calendar.getInstance()
                val max = list.maxByOrNull {
                    it.getFixedEndTime()
                }!!.getFixedEndTime().split("/")
                maxCal.set(max[0].toInt(), max[1].toInt() - 1, max[2].toInt(), 0, 0, 0)
                binding.calendarView.setMaximumDate(maxCal)
                //设置最小值
                minCal = Calendar.getInstance()
                val min = list.minByOrNull {
                    it.getFixedStartTime()
                }!!.getFixedStartTime().split("/")
                minCal.set(min[0].toInt(), min[1].toInt() - 1, min[2].toInt(), 0, 0, 0)
                binding.calendarView.setMinimumDate(minCal)
                //显示事件列表
                showDayEvents(cal)
            }
        })
    }

    //显示事件
    private fun showDayEvents(calendar: Calendar) {
        if (calendar.time <= maxCal.time && calendar.time >= minCal.time) {
            binding.calendarView.setDate(calendar)
            //获取年月日
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            if (!(year == cal.get(Calendar.YEAR)
                        && month == cal.get(Calendar.MONTH) + 1
                        && dayOfMonth == cal.get(Calendar.DAY_OF_MONTH))
            ) {
                binding.fabToday.show()
            } else {
                binding.fabToday.hide()
            }
            mMonth = month
            addIcon(year, month)
            //筛选点击的日期
            val selectDay = "$year/$month/$dayOfMonth"
            val eventData = events.filter {
                val sd = it.getFixedStartTime()
                val ed = it.getFixedEndTime()
                selectDay.daysInt(sd) >= 0 && ed.daysInt(selectDay) >= 0
            }
            //显示数据
            adapter.submitList(eventData)
            binding.loading.visibility = View.GONE
            //设置标题
            binding.currentDate.text =
                resources.getString(R.string.date_m_d, month.toString(), dayOfMonth.toString())
            binding.events.smoothScrollToPosition(0)
        } else {
            ToastUtil.short("所选日期无活动信息~")
        }
    }

    //添加事件图标
    private fun addIcon(year: Int, month: Int) {
        val eventDays = arrayListOf<EventDay>()
        for (i in 1..31) {
            val sc = Calendar.getInstance()
            sc.set(year, month - 1, i, 0, 0, 0)
            val selectDay = "$year/$month/$i"
            //获取对应日期的所有事件
            val eventData = events.filter {
                val sd = it.getFixedStartTime()
                val ed = it.getFixedEndTime()
                selectDay.daysInt(sd) >= 0 && ed.daysInt(selectDay) >= 0
            }
            var eventCount = 0
            var masterIcon = 0
            eventData.forEach {
                it.type.split("-").forEach { c ->
                    when (c.toInt()) {
                        31, 32, 39, 34, 37, 38, 45 -> eventCount++
                        in 90..101 -> {
                            if (masterIcon == 0) {
                                masterIcon++
                                eventCount++
                            }
                        }
                    }
                }
            }
            eventDays.add(
                EventDay(
                    sc,
                    getDrawableText(
                        requireContext(),
                        eventCount.toString(),
                        R.color.colorPrimaryDark,
                        10
                    )
                )
            )
        }

        binding.calendarView.setEvents(eventDays)

    }

}