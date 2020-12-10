package cn.wthee.pcrtool.ui.tool.calendar

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
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
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolCalendarBinding.inflate(inflater, container, false)

        //默认
        val cal = Calendar.getInstance()
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
        //选择监听显示数据
        binding.calendarView.apply {
            setDate(cal)
            //最大日期
            setMaximumDate(cal)
            //最小日期
            minCal = Calendar.getInstance()
            minCal.set(2020, 6 - 1, 6)
            setMinimumDate(minCal)
            setOnDayClickListener(object : OnDayClickListener {
                override fun onDayClick(eventDay: EventDay) {
                    showDayEvents(eventDay.calendar)
                }
            })
            //月份切换监听
            setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
                override fun onChange() {
                    mMonth++
                    if (mMonth > 12) {
                        mMonth = 1
                        mYear++
                    }
                    addIcon(mYear, mMonth)
                }
            })
            setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
                override fun onChange() {
                    mMonth--
                    if (mMonth < 1) {
                        mMonth = 12
                        mYear--
                    }
                    addIcon(mYear, mMonth)
                }
            })
        }

        //回到今天
        binding.fabToday.setOnClickListener {
            showDayEvents(cal)
        }

        MainScope().launch {
            getEvents(object : OnLoadFinish {
                override fun finish(maxDate: String) {
                    //设置最大值
                    val maxCal = Calendar.getInstance()
                    val date = maxDate.split("/")
                    maxCal.set(date[0].toInt(), date[1].toInt() - 1, date[2].toInt())
                    binding.calendarView.setMaximumDate(maxCal)
                    //显示事件列表
                    showDayEvents(cal)
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
        binding.calendarView.setDate(calendar)
        //获取年月日
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
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
    }

    //添加事件图标
    private fun addIcon(year: Int, month: Int) {
        Log.e("addIcon", "$year,$month")
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
                    getDrawableText(eventCount.toString(), R.color.colorPrimaryDark)
                )
            )
        }
        binding.calendarView.setEvents(events)

    }

    interface OnLoadFinish {
        fun finish(maxDate: String)
    }

    //文本转 drawable
    private fun getDrawableText(
        text: String,
        colorId: Int
    ): Drawable {
        val bitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = ResourcesUtil.getColor(colorId)
        val scale: Float = resources.displayMetrics.density
        paint.textSize = 10 * scale
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val x = (bitmap.width - bounds.width()) / 2f
        val y = (bitmap.height + bounds.height()) / 2f
        canvas.drawText(text, x, y, paint)
        return BitmapDrawable(resources, bitmap)
    }


}