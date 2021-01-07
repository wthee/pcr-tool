package cn.wthee.pcrtool.ui.tool.pvp

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wthee.pcrtool.adapter.PvpCharacterResultAdapter
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.databinding.FragmentToolPvpResultBinding
import cn.wthee.pcrtool.ui.common.CommonBottomSheetDialogFragment
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.gson.JsonArray
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/**
 * 查询结果
 */
class PvpResultDialogFragment : CommonBottomSheetDialogFragment() {

    private lateinit var binding: FragmentToolPvpResultBinding
    private lateinit var job: Job
    private var idList = JsonArray()

    companion object {
        fun getInstance(ids: String) =
            PvpResultDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("ids", ids)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            val ids = getString("ids")!!
            for (id in ids.split("-")) {
                if (id != "") {
                    idList.add(id.toInt())
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolPvpResultBinding.inflate(inflater, container, false)
        //创建服务
        job = MainScope().launch {
            try {
                binding.pvpNoData.visibility = View.GONE
                val result = MyAPIRepository.getPVPData(idList)
                if (result.status == 0) {
                    if (result.data!!.isEmpty()) {
                        binding.pvpNoData.visibility = View.VISIBLE
                    }
                    val adapter = PvpCharacterResultAdapter(false)
                    binding.pvpResultList.adapter = adapter
                    //展示查询结果
                    adapter.submitList(result.data!!.sortedByDescending {
                        it.up
                    })
                } else if (result.status == -1) {
                    ToastUtil.short(result.message)
                    dialog?.dismiss()
                }
                binding.progress.visibility = View.GONE
            } catch (e: Exception) {
            }
        }
        //toolbar
        ToolbarUtil(binding.pvpResultToolbar).setCenterTitle("进攻方信息")
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!job.isCancelled) {
            job.cancel()
        }
    }
}