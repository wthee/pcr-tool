package cn.wthee.pcrtool.ui.tool.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CalendarEventAdapter
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.network.model.CalendarData
import cn.wthee.pcrtool.databinding.FragmentToolCalendarBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * 日历
 */
class CalendarFragment : Fragment() {


    private lateinit var binding: FragmentToolCalendarBinding
    private lateinit var adapter: CalendarEventAdapter
    private var mMonth = 1
    private var events = listOf<CalendarData>()

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
            MainScope().launch {
                if (mMonth != month + 1) {
                    binding.loading.root.visibility = View.VISIBLE
                    getMonthEvents(year, month + 1, object : OnLoadFinish {
                        override fun finish() {
                            showDayEvents(dayOfMonth)
                        }
                    })
                } else {
                    showDayEvents(dayOfMonth)
                }
            }


        }
        //默认
        val cal = Calendar.getInstance()
        cal.time = Date(System.currentTimeMillis())
        val year = cal.get(Calendar.YEAR)
        mMonth = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        MainScope().launch {
            getMonthEvents(year, mMonth, object : OnLoadFinish {
                override fun finish() {
                    showDayEvents(day)
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
    private suspend fun getMonthEvents(year: Int, month: Int, onLoadFinish: OnLoadFinish) {
        val list = MyAPIRepository.getCalendar(year, month, 0)
        if (list.status == 0) {
            mMonth = month
            events = list.data!!
            onLoadFinish.finish()
        } else if (list.status == -1) {
            ToastUtil.short(list.message)
        }
    }

    private fun showDayEvents(dayOfMonth: Int) {
        val eventData = events.filter {
            it.date.split("/")[2].toInt() == dayOfMonth
        }
        adapter.addHeaderAndSubmitList(eventData)
        binding.loading.root.visibility = View.GONE
    }

    interface OnLoadFinish {
        fun finish()
    }

}