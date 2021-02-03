package cn.wthee.pcrtool.ui.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.NoticeListAdapter
import cn.wthee.pcrtool.databinding.FragmentNoticeListBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarHelper

/**
 * 消息通知列表
 *
 * 页面布局 [FragmentNoticeListBinding]
 *
 * ViewModels [NoticeViewModel]
 */
class NoticeListFragment : Fragment() {

    private lateinit var binding: FragmentNoticeListBinding
    private val noticeViewModel by activityViewModels<NoticeViewModel>()
    private lateinit var adapter: NoticeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FabHelper.addBackFab()
        MainActivity.fabNotice.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoticeListBinding.inflate(inflater, container, false)
        adapter = NoticeListAdapter(requireContext(), parentFragmentManager)
        binding.toolList.adapter = adapter
        //设置头部
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_notice,
            getString(R.string.notice)
        )
        //新闻数据
        noticeViewModel.getNotice()
        noticeViewModel.notice.observe(viewLifecycleOwner, {
            if (it.status == 0) {
                adapter.submitList(it.data)
                binding.loading.text = ""
            } else if (it.status == -1) {
                ToastUtil.short(it.message)
            }
        })
        return binding.root
    }
}