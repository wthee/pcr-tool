package cn.wthee.pcrtool.ui.tool.guild

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.GuildAdapter
import cn.wthee.pcrtool.databinding.FragmentToolGachaBinding
import cn.wthee.pcrtool.databinding.FragmentToolGuildBinding
import cn.wthee.pcrtool.ui.home.CharacterViewModel
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarHelper
import kotlinx.coroutines.launch

/**
 * 公会
 *
 * 页面布局 [FragmentToolGachaBinding]
 *
 * ViewModels [CharacterViewModel]
 */
class GuildFragment : Fragment() {

    private lateinit var binding: FragmentToolGuildBinding
    private val viewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolGuildBinding.inflate(inflater, container, false)
        val adapter = GuildAdapter()
        binding.toolList.adapter = adapter
        lifecycleScope.launch {
            val guilds = viewModel.getGuilds()
            adapter.submitList(guilds)
        }
        //设置头部
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_gacha,
            getString(R.string.title_guild)
        )
        //回到顶部
        binding.fabTop.setOnClickListener {
            binding.root.transitionToStart()
            binding.toolList.scrollToPosition(0)
        }
        return binding.root
    }


}