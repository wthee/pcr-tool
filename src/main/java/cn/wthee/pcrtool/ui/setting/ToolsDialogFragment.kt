package cn.wthee.pcrtool.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.Item
import cn.wthee.pcrtool.adapters.ToolListAdapter
import cn.wthee.pcrtool.databinding.FragmentToolsDialogBinding
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ToolsDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentToolsDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolsDialogBinding.inflate(inflater, container, false)
        val toolbar = ToolbarUtil(binding.tool)
        toolbar.setCenterStyle()
        toolbar.setTitle("工具")
        toolbar.hideRightIcon()
        toolbar.leftIcon.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val items = arrayListOf(
            Item("等级经验", R.drawable.ic_character),
            Item("RANK对比", R.drawable.ic_equip),
            Item("即将更新", R.drawable.ic_enemy)
        )
        val adapter = ToolListAdapter()
        binding.list.adapter = adapter
        adapter.submitList(items)

    }


    companion object {

        fun newInstance(): ToolsDialogFragment = ToolsDialogFragment()

    }
}