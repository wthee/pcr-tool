package cn.wthee.pcrtool.ui.tool.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.EventHistoryAdapter
import cn.wthee.pcrtool.databinding.FragmentToolEventBinding
import cn.wthee.pcrtool.ui.common.CommonListFragment
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarHelper

/**
 * 活动
 *
 * 页面布局 [FragmentToolEventBinding]
 *
 * ViewModels [EventViewModel]
 */
class EventFragment : CommonListFragment() {

    private val viewModel: EventViewModel by activityViewModels {
        InjectorUtil.provideEventViewModelFactory()
    }
    private lateinit var binding: FragmentToolEventBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolEventBinding.inflate(inflater, container, false)
        val adapter = EventHistoryAdapter()
        binding.toolList.adapter = adapter
        viewModel.getEventHistory()
        viewModel.events.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        //设置头部
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_event,
            getString(R.string.tool_event)
        )
        return binding.root
    }

}