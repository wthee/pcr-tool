package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.PvpCharacterResultAdapter
import cn.wthee.pcrtool.data.model.Data
import cn.wthee.pcrtool.databinding.FragmentToolPvpResultBinding
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
        val results = requireArguments().getSerializable(RESULT) as Data
        val adapter = PvpCharacterResultAdapter()
        binding.list.adapter = adapter
        adapter.submitList(results.result)
        //toolbar
        val toolbar = ToolbarUtil(binding.pvpResultToolbar)
        toolbar.title.text = "进攻方信息"
        toolbar.hideRightIcon()
        toolbar.setLeftIcon(R.drawable.ic_back)
        toolbar.setRightIcon(R.drawable.ic_detail_share)
        toolbar.setCenterStyle()
        toolbar.leftIcon.setOnClickListener {
            dialog?.dismiss()
        }
        return binding.root
    }


    companion object {
        fun newInstance(results: Data): ToolPvpResultDialogFragment =
            ToolPvpResultDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(RESULT, results)
                }
            }
    }
}