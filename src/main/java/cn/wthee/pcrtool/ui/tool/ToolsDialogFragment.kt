package cn.wthee.pcrtool.ui.tool

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionManager
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterLevelExpAdapter
import cn.wthee.pcrtool.adapters.Item
import cn.wthee.pcrtool.adapters.ToolListAdapter
import cn.wthee.pcrtool.databinding.FragmentToolsDialogBinding
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ToolsDialogFragment : BottomSheetDialogFragment() {


    companion object {
        var isShownDetail = false
        var toolPosition = 0
    }

    private lateinit var binding: FragmentToolsDialogBinding

    private val sharedViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

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
            if (isShownDetail) {
                isShownDetail = false
                //过渡动画
                val transform = MaterialContainerTransform().apply {
                    startView = binding.toolHead.root
                    endView =
                        binding.list.findViewHolderForAdapterPosition(0)?.itemView?.findViewById(R.id.tool_root)
                    addTarget(endView!!)
                    setPathMotion(MaterialArcMotion())
                    scrimColor = Color.TRANSPARENT
                }
                TransitionManager.beginDelayedTransition(binding.root, transform)
                //布局显示/隐藏
                binding.list.visibility = View.VISIBLE
                binding.toolContent.visibility = View.GONE
            } else {
                dismiss()
            }
        }
        //角色经验
        MainScope().launch {
            val list = sharedViewModel.getLevelExp() as MutableList
            val adapter = CharacterLevelExpAdapter()
            binding.listLevel.adapter = adapter
            adapter.submitList(list)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val items = arrayListOf(
            Item("等级经验", R.drawable.ic_character),
            Item("即将更新", R.drawable.ic_enemy)
        )
        val adapter = ToolListAdapter(binding)
        binding.list.adapter = adapter
        adapter.submitList(items)
    }

}