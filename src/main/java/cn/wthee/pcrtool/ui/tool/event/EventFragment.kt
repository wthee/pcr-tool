package cn.wthee.pcrtool.ui.tool.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.EventHistoryAdapter
import cn.wthee.pcrtool.databinding.FragmentToolEventBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.RecyclerViewHelper.setScrollToTopListener
import cn.wthee.pcrtool.utils.ToolbarUtil

/**
 * 活动
 */
class EventFragment : Fragment() {

    private lateinit var viewModel: EventViewModel
    private lateinit var binding: FragmentToolEventBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolEventBinding.inflate(inflater, container, false)
        val adapter = EventHistoryAdapter()
        binding.eventList.adapter = adapter

        viewModel = InjectorUtil.provideEventViewModelFactory().create(EventViewModel::class.java)

        viewModel.getEventHistory()
        viewModel.events.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        //设置头部
        ToolbarUtil(binding.toolEvent).setMainToolbar(
            R.drawable.ic_event,
            getString(R.string.tool_event)
        )

        //滚动监听
        binding.eventList.setScrollToTopListener(binding.fabTop)
        return binding.root
    }

}