package cn.wthee.pcrtool.ui.notice

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import cn.wthee.pcrtool.data.network.model.AppNotice
import cn.wthee.pcrtool.databinding.FragmentNoticeDetailBinding
import cn.wthee.pcrtool.ui.common.CommonBottomSheetDialogFragment


private const val NOTICE = "notice"

/**
 * 公告详情
 *
 * 根据 [url] 加载公告
 *
 * 页面布局 [FragmentNoticeDetailBinding]
 *
 * ViewModels []
 */
class NoticeDetailDialogFragment : CommonBottomSheetDialogFragment() {

    private lateinit var notice: AppNotice
    private lateinit var binding: FragmentNoticeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notice = requireArguments().getSerializable(NOTICE) as AppNotice
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoticeDetailBinding.inflate(inflater, container, false)
        binding.apply {
            title.text = if (notice.type == 1)
                notice.title + " 更新日志"
            else
                notice.title
            date.text = notice.date
            content.text = notice.message
        }
        return binding.root
    }


    companion object {
        @JvmStatic
        fun getInstance(notice: AppNotice) =
            NoticeDetailDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(NOTICE, notice)
                }
            }
    }
}