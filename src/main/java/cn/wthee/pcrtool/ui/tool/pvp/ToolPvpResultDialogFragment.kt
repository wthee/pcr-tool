package cn.wthee.pcrtool.ui.tool.pvp

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wthee.pcrtool.adapters.PvpCharacterResultAdapter
import cn.wthee.pcrtool.data.MyAPIRepository
import cn.wthee.pcrtool.databinding.FragmentToolPvpResultBinding
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ToolPvpResultDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentToolPvpResultBinding
    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolPvpResultBinding.inflate(inflater, container, false)
        //创建服务
        //展示查询结果
        job = MainScope().launch {
            binding.pvpNoData.visibility = View.GONE
            val data = MyAPIRepository.getPVPData()
            if (data.isEmpty()) {
                binding.pvpNoData.visibility = View.VISIBLE
            } else {
                binding.loadingDialog.visibility = View.GONE
                val adapter = PvpCharacterResultAdapter(requireActivity())
                binding.list.adapter = adapter
                adapter.submitList(data.sortedByDescending {
                    it.up
                })
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