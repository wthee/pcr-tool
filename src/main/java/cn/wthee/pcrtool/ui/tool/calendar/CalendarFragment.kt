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
import com.applandeo.materialcalendarview.CalendarUtils.getDrawableText
import com.applandeo.materialcalendarview.EventDay
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*


/**
 * 日历
 */
class CalendarFragment : Fragment() {


    private lateinit var binding: FragmentToolCalendarBinding
    private lateinit var adapter: CalendarEventAdapter
    private var events = listOf<CalendarDay>()
    private var mYear = 0
    private var mMonth = 0
    lateinit var minCal: Calendar
    lateinit var maxCal: Calendar
    lateinit var cal: Calendar
    lateinit var job: Job

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
        ToolbarUtil(binding.toolCalendar).setToolHead(
            R.drawable.ic_calender,
            getString(R.string.tool_calendar)
        )

        return binding.root

    }

    override fun onDestroy() {
        super.onDestroy()
        if (!job.isCancelled) {
            job.cancel()
        }
    }

    private fun setListener() {
        binding.calendarView.apply {
            setDate(cal)
            //最大日期
            setMaximumDate(cal)
            //最小日期
            minCal = Calendar.getInstance()
            minCal.set(2020, 6 - 1, 6)
            setMinimumDate(minCal)
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
        binding.currentDate.text = "${month} 月 $day 日"
        //列表
        adapter = CalendarEventAdapter()
        binding.events.adapter = adapter
        //初始加载
        job = MainScope().launch {
            getEvents(object : OnLoadFinish {
                override fun finish(maxDate: String) {
                    binding.calendarView.visibility = View.VISIBLE
                    //设置最大值
                    maxCal = Calendar.getInstance()
                    val date = maxDate.split("/")
                    maxCal.set(date[0].toInt(), date[1].toInt() - 1, date[2].toInt())
                    binding.calendarView.setMaximumDate(maxCal)
                    //显示事件列表
                    showDayEvents(cal)
                }
            })
        }
    }

    //一次获取全部
    private suspend fun getEvents(onLoadFinish: OnLoadFinish) {
        val list = MyAPIRepository.getCalendar()
        if (list.status == 0) {
            events = list.data!!.days
            onLoadFinish.finish(list.data!!.maxDate)
        } else if (list.status == -1) {
            ToastUtil.short(list.message)
        }
    }

    //显示事件
    private fun showDayEvents(calendar: Calendar) {
        if (calendar.time <= maxCal.time && calendar.time >= minCal.time) {
            binding.calendarView.setDate(cal)
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
            val eventData = events.find {
                it.date.split("/")[0].toInt() == year
                        && it.date.split("/")[1].toInt() == month
                        && it.date.split("/")[2].toInt() == dayOfMonth
            }?.content
            //显示数据
            adapter.addHeaderAndSubmitList(eventData)
            binding.loading.root.visibility = View.GONE
            //设置标题
            binding.currentDate.text = "$month 月 $dayOfMonth 日"
            binding.events.smoothScrollToPosition(0)
        } else {
            ToastUtil.short("所选日期无活动信息~")
        }
    }

    //添加事件图标
    private fun addIcon(year: Int, month: Int) {
        val eventData = events.filter {
            it.date.split("/")[0].toInt() == year
                    && it.date.split("/")[1].toInt() == month

        }
        val events = arrayListOf<EventDay>()
        eventData.forEach {
            var eventCount = 0
            it.content.forEach { c ->
                eventCount += c.events.size
            }
            val calendar = Calendar.getInstance()
            val date = it.date.split("/")
            calendar.set(date[0].toInt(), date[1].toInt() - 1, date[2].toInt())
            events.add(
                EventDay(
                    calendar,
                    getDrawableText(
                        requireContext(),
                        eventCount.toString(),
                        R.color.colorPrimaryDark,
                        10
                    )
                )
            )
        }
        binding.calendarView.setEvents(events)

    }

    interface OnLoadFinish {
        fun finish(maxDate: String)
    }


}