package cn.wthee.pcrtool.ui.tool.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.adapter.EventStoryAdapter
import cn.wthee.pcrtool.databinding.FragmentEventStoryDetailsBinding
import cn.wthee.pcrtool.ui.common.CommonBottomSheetDialogFragment
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.deleteSpace


private const val STORY_ID = "storyId"
private const val STORY_NAME = "storyName"

/**
 * 剧情故事详情页面
 *
 * 根据 [storyId] 显示剧情信息
 *
 * 页面布局 [FragmentEventStoryDetailsBinding]
 *
 * ViewModels [EventViewModel]
 */
class EventStoryDetailsDialogFragment : CommonBottomSheetDialogFragment() {

    companion object {
        fun getInstance(storyId: Int, name: String) =
            EventStoryDetailsDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(STORY_ID, storyId)
                    putString(STORY_NAME, name)
                }
            }
    }

    private var storyId = 0
    private var storyName = ""
    private lateinit var binding: FragmentEventStoryDetailsBinding
    private lateinit var adapter: EventStoryAdapter

    private val viewModel by activityViewModels<EventViewModel> {
        InjectorUtil.provideEventViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            storyId = it.getInt(STORY_ID)
            storyName = it.getString(STORY_NAME).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventStoryDetailsBinding.inflate(inflater, container, false)
        init()
        setObserve()
        viewModel.getStoryDetails(storyId)
        return binding.root
    }

    private fun init() {
        binding.apply {
            adapter = EventStoryAdapter()
            storyList.adapter = adapter
            toolbarTitle.text = storyName
        }
    }

    private fun setObserve() {
        viewModel.storys.observe(viewLifecycleOwner) {
            adapter.submitList(it.map { d ->
                d.title = d.title.deleteSpace().replace(storyName, "")
                d
            })
        }
    }
}