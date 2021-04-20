package cn.wthee.pcrtool.ui.tool.clan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CallBack
import cn.wthee.pcrtool.adapter.ClanAdapter
import cn.wthee.pcrtool.databinding.FragmentToolClanBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToolbarHelper
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

/**
 * 团队战
 *
 * 页面布局 [FragmentToolClanBinding]
 *
 * ViewModels [ClanViewModel]
 */
@AndroidEntryPoint
class ClanFragment : Fragment() {

    companion object {
        var clickIndex = 0
    }

    private val viewModel: ClanViewModel by viewModels()
    private lateinit var binding: FragmentToolClanBinding
    private lateinit var adapter: ClanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true).apply {
            duration = 500L
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false).apply {
            duration = 500L
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.state =
            (binding.toolList.layoutManager as LinearLayoutManager).onSaveInstanceState()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()

        binding = FragmentToolClanBinding.inflate(inflater, container, false)
        //加载缓存
        if (viewModel.state != null) {
            postponeEnterTransition()

            (binding.toolList.layoutManager as LinearLayoutManager).onRestoreInstanceState(viewModel.state)
        } else {
            viewModel.getAllClanBattleData()
        }
        adapter = ClanAdapter(object : CallBack {
            override fun todo(data: Any?) {
                startPostponedEnterTransition()
            }
        })
        binding.toolList.adapter = adapter
        binding.toolList.setItemViewCacheSize(Int.MAX_VALUE)
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