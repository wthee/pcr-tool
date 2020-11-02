package cn.wthee.pcrtool.ui.tool.level

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterLevelExpAdapter
import cn.wthee.pcrtool.databinding.FragmentToolLevelBinding
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ResourcesUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ToolLevelFragment : Fragment() {

    private lateinit var binding: FragmentToolLevelBinding
    private val sharedViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolLevelBinding.inflate(inflater, container, false)
        MainScope().launch {
            val list = sharedViewModel.getLevelExp() as MutableList
            val adapter = CharacterLevelExpAdapter()
            binding.listLevel.adapter = adapter
            adapter.submitList(list)
        }
        //设置头部
        binding.toolLevel.apply {
            toolIcon.setImageDrawable(ResourcesUtil.getDrawable(R.drawable.ic_level))
            toolTitle.text = getString(R.string.tool_level)
        }

        return binding.root
    }

}