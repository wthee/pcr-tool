package cn.wthee.pcrtool.ui.tool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.adapters.CharacterLevelExpAdapter
import cn.wthee.pcrtool.databinding.FragmentToolLevelBinding
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.utils.InjectorUtil
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
        binding.toolLevel.transitionName = "tool_level"
        MainScope().launch {
            val list = sharedViewModel.getLevelExp() as MutableList
            val adapter = CharacterLevelExpAdapter()
            binding.listLevel.adapter = adapter
            adapter.submitList(list)
        }
        return binding.root
    }

}