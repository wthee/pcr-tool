package cn.wthee.pcrtool.ui.tool.calendar

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CalendarEventAdapter
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.network.model.CalendarDay
import cn.wthee.pcrtool.databinding.FragmentToolCalendarBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
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
    private var mYear = 0
    private var mMonth = 0


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
        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val year = eventDay.calendar.get(Calendar.YEAR)
                val month = eventDay.calendar.get(Calendar.MONTH) + 1
                val day = eventDay.calendar.get(Calendar.DAY_OF_MONTH)
                mMonth = month
                showDayEvents(year, month, day)
                binding.currentDate.text = "$month 月 $day 日"
                binding.events.smoothScrollToPosition(0)
            }
        })
        binding.calendarView.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                mMonth++
                if (mMonth > 12) {
                    mMonth = 1
                    mYear++
                }
                addIcon(mYear, mMonth)
            }
        })
        binding.calendarView.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                mMonth--
                if (mMonth < 1) {
                    mMonth = 12
                    mYear--
                }
                addIcon(mYear, mMonth)
            }
        })
        //默认
        val cal = Calendar.getInstance()
        cal.time = Date(System.currentTimeMillis())
        val year = cal.get(Calendar.YEAR)
        mYear = year
        val month = cal.get(Calendar.MONTH) + 1
        mMonth = month
        val day = cal.get(Calendar.DAY_OF_MONTH)
        binding.currentDate.text = "${month} 月 $day 日"
        binding.calendarView.setMaximumDate(cal)
        val minCal = Calendar.getInstance()
        minCal.time = Date(
            SimpleDateFormat("yyyy/MM/dd")
                .parse("2020/06/06")
                .time
        )
        binding.calendarView.setMinimumDate(minCal)

        MainScope().launch {
            getMonthEvents(object : OnLoadFinish {
                override fun finish(maxDate: String) {
                    val maxCal = Calendar.getInstance()
                    maxCal.time = Date(
                        SimpleDateFormat("yyyy/MM/dd")
                            .parse(maxDate)
                            .time
                    )
                    binding.calendarView.setMaximumDate(maxCal)
                    addIcon(year, month)
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
        val eventData = events.find {
            it.date.split("/")[0].toInt() == year
                    && it.date.split("/")[1].toInt() == month
                    && it.date.split("/")[2].toInt() == dayOfMonth
        }?.content
        adapter.addHeaderAndSubmitList(eventData)
        binding.loading.root.visibility = View.GONE
    }

    private fun addIcon(year: Int, month: Int) {
        val eventData = events.filter {
            it.date.split("/")[0].toInt() == year
                    && it.date.split("/")[1].toInt() == month

        }
        val events = arrayListOf<EventDay>()
        eventData.forEach {
            val types = arrayListOf<String>()
            it.content.forEach { content ->
                types.add(content.type)
            }
            val calendar = Calendar.getInstance()
            calendar.time = Date(
                SimpleDateFormat("yyyy/MM/dd")
                    .parse(it.date)
                    .time
            )

            events.add(
                EventDay(
                    calendar,
                    getDrawableText(types.size.toString(), Typeface.DEFAULT, Color.RED, 10)
                )
            )
        }
        binding.calendarView.setEvents(events)

    }

    interface OnLoadFinish {
        fun finish(maxDate: String)
    }

    fun getDrawableText(
        text: String,
        typeface: Typeface?,
        color: Int,
        size: Int
    ): Drawable {
        val bitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = typeface ?: Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = ContextCompat.getColor(requireContext(), color)
        val scale: Float = resources.displayMetrics.density
        paint.textSize = size * scale
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val x = (bitmap.width - bounds.width()) / 2f
        val y = (bitmap.height + bounds.height()) / 2f
        canvas.drawText(text, x, y, paint)
        return BitmapDrawable(resources, bitmap)
    }


}