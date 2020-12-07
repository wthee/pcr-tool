package cn.wthee.pcrtool.ui.tool.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CalendarEventAdapter
import cn.wthee.pcrtool.data.MyAPIRepository
import cn.wthee.pcrtool.data.model.CalendarData
import cn.wthee.pcrtool.data.model.ResultData
import cn.wthee.pcrtool.databinding.FragmentToolCalendarBinding
import cn.wthee.pcrtool.enums.Response
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment() {

    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        val binding = FragmentToolCalendarBinding.inflate(inflater, container, false)
        //列表
        val adapter = CalendarEventAdapter()
        binding.events.adapter = adapter
        //设置最大
        val format = SimpleDateFormat("yyyy/MM/dd")
        val cal = Calendar.getInstance()
        cal.time = Date(System.currentTimeMillis())
        binding.calendarView.maxDate =
            format.parse("${cal.get(Calendar.YEAR)}/${cal.get(Calendar.MONTH) + 2}/1").time
        job = MainScope().launch {
            val list = MyAPIRepository.getCalendar()
            if (list.status == Response.SUCCESS) {
                if (list.data.size > 1) {
                    binding.calendarView.minDate = format.parse(list.data[0].startDate).time
                    binding.calendarView.maxDate =
                        format.parse(list.data[list.data.size - 1].endDate).time
                }

                showEvent(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH),
                    list,
                    adapter,
                    binding
                )
                //选择监听显示数据
                binding.calendarView.apply {
                    setOnDateChangeListener { view, year, month, dayOfMonth ->
                        showEvent(year, month, dayOfMonth, list, adapter, binding)
                    }
                }
            } else if (list.status == Response.FAILURE) {
                ToastUtil.short(list.message)
            }
        }
        //设置头部
        ToolbarUtil(binding.toolCalendar).setToolHead(
            R.drawable.ic_leader,
            getString(R.string.tool_leader)
        )

        return binding.root

    }

    private fun showEvent(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        list: ResultData<List<CalendarData>>,
        adapter: CalendarEventAdapter,
        binding: FragmentToolCalendarBinding
    ) {
        val selDate = "$year/${month + 1}/$dayOfMonth"
        var eventData = list.data.filter {
            it.date == selDate
        }
        if (eventData.isEmpty()) {
            eventData = arrayListOf(
                CalendarData(selDate, "", "", "无活动", "none")
            )
        }
        adapter.submitList(eventData)
        binding.loading.root.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        if (!job.isCancelled) {
            job.cancel()
        }
    }
}