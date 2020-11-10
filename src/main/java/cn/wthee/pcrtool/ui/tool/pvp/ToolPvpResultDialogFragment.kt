package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wthee.pcrtool.adapters.PvpCharacterResultAdapter
import cn.wthee.pcrtool.data.OnPostListener
import cn.wthee.pcrtool.data.PvpDataRepository
import cn.wthee.pcrtool.data.model.Result
import cn.wthee.pcrtool.databinding.FragmentToolPvpResultBinding
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


const val RESULT = "results"

class ToolPvpResultDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentToolPvpResultBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolPvpResultBinding.inflate(inflater, container, false)
        //创建服务
        PvpDataRepository.getData(object : OnPostListener {
            override fun success(data: List<Result>) {
                //展示查询结果
                if (data.isEmpty()) {
                    binding.pvpNoData.visibility = View.VISIBLE
                } else {
                    binding.pvpNoData.visibility = View.GONE
                    val adapter = PvpCharacterResultAdapter(requireActivity())
                    binding.list.adapter = adapter
                    adapter.submitList(data.sortedByDescending {
                        it.up
                    })
                }
                binding.pvpResultLoading.visibility = View.GONE
            }

            override fun error() {
                ToastUtil.short("查询失败，请检查网络~")
            }
        })

        //toolbar
        ToolbarUtil(binding.pvpResultToolbar).setCenterTitle("进攻方信息")
            .leftIcon.setOnClickListener {
                dialog?.dismiss()
            }
        return binding.root
    }

}