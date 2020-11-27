package cn.wthee.pcrtool.ui.tool.pvp

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wthee.pcrtool.adapters.PvpCharacterResultAdapter
import cn.wthee.pcrtool.data.MyAPIRepository
import cn.wthee.pcrtool.databinding.FragmentToolPvpResultBinding
import cn.wthee.pcrtool.enums.Response
import cn.wthee.pcrtool.ui.common.CommonBasicDialogFragment
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class PvpResultDialogFragment : CommonBasicDialogFragment() {

    private lateinit var binding: FragmentToolPvpResultBinding
    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolPvpResultBinding.inflate(inflater, container, false)
        binding.loading.root.setBackgroundColor(Color.TRANSPARENT)
        //创建服务
        //展示查询结果
        job = MainScope().launch {
            try {
                binding.pvpNoData.visibility = View.GONE
                val result = MyAPIRepository.getPVPData()
                if (result.status == Response.FAILURE) {
                    ToastUtil.short(result.message)
                    dialog?.dismiss()
                } else {
                    if (result.data.isEmpty()) {
                        binding.pvpNoData.visibility = View.VISIBLE
                    }
                    val adapter = PvpCharacterResultAdapter(requireActivity())
                    binding.list.adapter = adapter
                    adapter.submitList(result.data.sortedByDescending {
                        it.up
                    })
                }
                binding.loading.root.visibility = View.GONE
            } catch (e: Exception) {
            }
        }
        //toolbar
        ToolbarUtil(binding.pvpResultToolbar).setCenterTitle("进攻方信息")
            .leftIcon.setOnClickListener {
                dialog?.dismiss()
            }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!job.isCancelled) {
            job.cancel()
        }
    }
}