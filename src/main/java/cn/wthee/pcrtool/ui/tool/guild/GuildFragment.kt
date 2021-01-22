package cn.wthee.pcrtool.ui.tool.guild

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.GuildAdapter
import cn.wthee.pcrtool.databinding.FragmentToolGachaBinding
import cn.wthee.pcrtool.databinding.FragmentToolGuildBinding
import cn.wthee.pcrtool.ui.common.CommonListFragment
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarHelper

/**
 * 公会
 *
 * 页面布局 [FragmentToolGachaBinding]
 *
 * ViewModels [GuildViewModel]
 */
class GuildFragment : CommonListFragment() {

    private lateinit var binding: FragmentToolGuildBinding
    private val viewModel by activityViewModels<GuildViewModel> {
        InjectorUtil.provideGuildViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolGuildBinding.inflate(inflater, container, false)
        val adapter = GuildAdapter()
        binding.toolList.adapter = adapter
        viewModel.getGuilds()
        viewModel.guilds.observe(viewLifecycleOwner, {
            adapter.submitList(it)

        })
        //设置头部
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_guild,
            getString(R.string.title_guild)
        )
        return binding.root
    }


}