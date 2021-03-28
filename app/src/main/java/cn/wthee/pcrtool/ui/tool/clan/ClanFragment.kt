package cn.wthee.pcrtool.ui.tool.clan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.ClanAdapter
import cn.wthee.pcrtool.databinding.FragmentToolClanBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarHelper
import cn.wthee.pcrtool.viewmodel.ClanViewModel

/**
 * 团队战
 *
 * 页面布局 [FragmentToolClanBinding]
 *
 * ViewModels [ClanViewModel]
 */
class ClanFragment : Fragment() {

    private val viewModel by activityViewModels<ClanViewModel> {
        InjectorUtil.provideClanViewModelFactory()
    }
    private lateinit var binding: FragmentToolClanBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolClanBinding.inflate(inflater, container, false)

        val adapter = ClanAdapter()
        binding.toolList.adapter = adapter

        viewModel.getAllClanBattleData()
        viewModel.clanInfo.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        //设置头部
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_def,
            getString(R.string.tool_clan)
        )
        return binding.root
    }


}