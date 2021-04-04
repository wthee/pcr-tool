package cn.wthee.pcrtool.ui.tool.clan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    companion object {
        var clickIndex = 0
    }

    private lateinit var viewModel: ClanViewModel
    private lateinit var binding: FragmentToolClanBinding
    private lateinit var adapter: ClanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolClanBinding.inflate(inflater, container, false)
        viewModel = InjectorUtil.provideClanViewModelFactory().create(ClanViewModel::class.java)
        adapter = ClanAdapter()
        binding.toolList.adapter = adapter
        binding.toolList.setItemViewCacheSize(Int.MAX_VALUE)
        viewModel.getAllClanBattleData()
        viewModel.clanInfo.observe(viewLifecycleOwner) {
            adapter.submitList(it) {
                binding.toolList.scrollToPosition(clickIndex)
                startPostponedEnterTransition()
            }
        }
        //设置头部
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_def,
            getString(R.string.tool_clan)
        )
        postponeEnterTransition()
        return binding.root
    }


}