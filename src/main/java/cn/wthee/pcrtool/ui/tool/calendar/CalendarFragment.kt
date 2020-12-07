package cn.wthee.pcrtool.ui.tool.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CalendarEventAdapter
import cn.wthee.pcrtool.data.MyAPIRepository
import cn.wthee.pcrtool.databinding.FragmentToolCalendarBinding
import cn.wthee.pcrtool.enums.Response
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class CalendarFragment : Fragment() {

    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        val binding = FragmentToolCalendarBinding.inflate(inflater, container, false)
        val adapter = CalendarEventAdapter()
        binding.events.adapter = adapter
        job = MainScope().launch {
            val list = MyAPIRepository.getCalendar()
            if (list.status == Response.SUCCESS) {
                //TODO 显示数据
                binding.calendarView.apply {
                    setOnDateChangeListener { view, year, month, dayOfMonth ->
                        val eventData = list.data.filter {
                            it.date == "$year/$month/$dayOfMonth"
                        }
                        adapter.submitList(eventData) {
                            adapter.notifyDataSetChanged()
                        }
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

    override fun onStop() {
        super.onStop()
        if (!job.isCancelled) {
            job.cancel()
        }
    }
}