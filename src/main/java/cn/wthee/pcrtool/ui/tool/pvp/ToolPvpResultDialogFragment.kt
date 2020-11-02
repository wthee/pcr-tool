package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wthee.pcrtool.adapters.PvpCharacterResultAdapter
import cn.wthee.pcrtool.data.OnPostListener
import cn.wthee.pcrtool.data.PvpDataRepository
import cn.wthee.pcrtool.data.model.PVPData
import cn.wthee.pcrtool.databinding.FragmentToolPvpResultBinding
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Response


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
            override fun success(data: Response<PVPData>) {
                try {
                    val responseBody = data.body()
                    if (responseBody == null || responseBody.code != 0) {
                        ToastUtil.short("查询异常，请重试~")
                    } else {
                        //展示查询结果
                        if (responseBody.data.result.isEmpty()) {
                            binding.pvpNoData.visibility = View.VISIBLE
                        } else {
                            binding.pvpNoData.visibility = View.GONE
                            val adapter = PvpCharacterResultAdapter(requireActivity())
                            binding.list.adapter = adapter
                            adapter.submitList(responseBody.data.result)
                        }
                    }
                } catch (e: Exception) {
                    ToastUtil.short("数据解析失败，请重试~")
                }
                binding.pvpResultLoading.visibility = View.GONE
            }

            override fun error() {
                ToastUtil.short("查询失败，请检查网络~")
            }
        })

        //toolbar
        val toolbar = ToolbarUtil(binding.pvpResultToolbar)
        toolbar.title.text = "进攻方信息"
        //TODO 分享
//        toolbar.setRightIcon(R.drawable.ic_detail_share)
        toolbar.setCenterStyle()
        toolbar.leftIcon.setOnClickListener {
            dialog?.dismiss()
        }
        return binding.root
    }

}