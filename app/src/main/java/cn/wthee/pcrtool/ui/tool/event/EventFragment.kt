package cn.wthee.pcrtool.ui.tool.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CallBack
import cn.wthee.pcrtool.adapter.EventHistoryAdapter
import cn.wthee.pcrtool.data.view.EventData
import cn.wthee.pcrtool.databinding.FragmentToolEventBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToolbarHelper
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.viewmodel.EventViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 活动
 *
 * 页面布局 [FragmentToolEventBinding]
 *
 * ViewModels [EventViewModel]
 */
@AndroidEntryPoint
class EventFragment : Fragment() {

    private val viewModel: EventViewModel by activityViewModels()
    private lateinit var binding: FragmentToolEventBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolEventBinding.inflate(inflater, container, false)
        val adapter = EventHistoryAdapter(object : CallBack {
            //点击回调，查看活动剧情
            override fun todo(data: Any?) {
                data?.let {
                    val event = data as EventData
                    EventStoryDetailsDialogFragment.getInstance(
                        event.storyId,
                        event.title.deleteSpace()
                    ).show(parentFragmentManager, "story_detail")
                }
            }
        })
        binding.toolList.adapter = adapter
        viewModel.getEventHistory()
        viewModel.events.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        //设置头部
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_event,
            getString(R.string.tool_event)
        )
        return binding.root
    }

}