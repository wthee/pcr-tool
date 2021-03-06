package cn.wthee.pcrtool.ui.tool.pvp

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.adapter.PvpResultAdapter
import cn.wthee.pcrtool.adapter.PvpResultItemAdapter
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.databinding.FragmentToolPvpResultBinding
import cn.wthee.pcrtool.ui.common.CommonBottomSheetDialogFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.viewmodel.PvpLikedViewModel
import com.google.gson.JsonArray
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 竞技场查询结果弹窗
 *
 * 根据防守方id [defIds]，查询信息
 *
 * 页面布局 [FragmentToolPvpResultBinding]
 *
 * ViewModels [PvpLikedViewModel]
 */
class PvpResultDialogFragment : CommonBottomSheetDialogFragment() {

    private lateinit var binding: FragmentToolPvpResultBinding
    private lateinit var job: Job
    private var idList = JsonArray()
    private var defIds = arrayListOf<Int>()
    private val viewModel by activityViewModels<PvpLikedViewModel> {
        InjectorUtil.providePvpViewModelFactory()
    }

    companion object {
        fun getInstance(defIds: String) =
            PvpResultDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("defIds", defIds)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            val ids = getString("defIds")!!
            for (id in ids.split("-")) {
                if (id != "") {
                    idList.add(id.toInt())
                    defIds.add(id.toInt())
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolPvpResultBinding.inflate(inflater, container, false)
        //显示进攻队伍
        val defAdapter = PvpResultItemAdapter()
        binding.defCharacters.adapter = defAdapter
        defAdapter.submitList(defIds)
        //创建服务
        job = lifecycleScope.launch {
            try {
                binding.pvpNoData.visibility = View.GONE
                val result = MyAPIRepository.getInstance().getPVPData(idList)
                if (result.status == 0) {
                    if (result.data!!.isEmpty()) {
                        binding.pvpNoData.visibility = View.VISIBLE
                    }
                    val adapter = PvpResultAdapter(false, viewModel)
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
                MainScope().launch {
                    UMCrash.generateCustomLog(e, Constants.EXCEPTION_PVP_DIALOG)
                }
            }
        }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        try {
            if (!job.isCompleted) {
                job.cancel()
            }
        } catch (e: Exception) {

        }
    }
}