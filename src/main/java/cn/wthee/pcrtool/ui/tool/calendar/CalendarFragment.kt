package cn.wthee.pcrtool.ui.tool.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CalendarEventAdapter
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.databinding.FragmentToolCalendarBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 日历
 */
class CalendarFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        val binding = FragmentToolCalendarBinding.inflate(inflater, container, false)
        //列表
        val adapter = CalendarEventAdapter()
        binding.events.adapter = adapter
        //选择监听显示数据
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            MainScope().launch {
                val list = MyAPIRepository.getCalendar(year, month + 1, dayOfMonth)
                if (list.status == 0) {
                    val eventData = list.data!!.filter {
                        it.date.split("/")[2].toInt() == dayOfMonth
                    }
                    adapter.addHeaderAndSubmitList(eventData)
                    binding.loading.root.visibility = View.GONE
                } else if (list.status == -1) {
                    ToastUtil.short(list.message)
                }
            }
        }
        //默认点击
        binding.calendarView.date = System.currentTimeMillis()

        //设置头部
        ToolbarUtil(binding.toolCalendar).setToolHead(
            R.drawable.ic_leader,
            getString(R.string.tool_leader)
        )

        return binding.root

    }


}