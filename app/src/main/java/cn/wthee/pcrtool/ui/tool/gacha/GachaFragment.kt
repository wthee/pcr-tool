package cn.wthee.pcrtool.ui.tool.gacha

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.GachaHistoryAdapter
import cn.wthee.pcrtool.databinding.FragmentToolGachaBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToolbarHelper
import cn.wthee.pcrtool.viewmodel.GachaViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 卡池
 *
 * 页面布局 [FragmentToolGachaBinding]
 *
 * ViewModels [GachaViewModel]
 */
@AndroidEntryPoint
class GachaFragment : Fragment() {

    private val viewModel: GachaViewModel by activityViewModels()
    private lateinit var binding: FragmentToolGachaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolGachaBinding.inflate(inflater, container, false)
        val adapter = GachaHistoryAdapter()
        binding.toolList.adapter = adapter
        viewModel.getGachaHistory()
        viewModel.gachas.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        //设置头部
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_gacha,
            getString(R.string.tool_gacha)
        )
        return binding.root
    }


}