package cn.wthee.pcrtool.ui.tool.gacha

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.GachaHistoryAdapter
import cn.wthee.pcrtool.databinding.FragmentToolGachaBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.RecyclerViewHelper.setScollToTopListener
import cn.wthee.pcrtool.utils.ToolbarUtil

/**
 * 卡池
 */
class GachaFragment : Fragment() {

    private lateinit var viewModel: GachaViewModel
    private lateinit var binding: FragmentToolGachaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolGachaBinding.inflate(inflater, container, false)
        val adapter = GachaHistoryAdapter()
        binding.gachaList.adapter = adapter

        viewModel = InjectorUtil.provideGachaViewModelFactory().create(GachaViewModel::class.java)

        viewModel.getGachaHistory()
        viewModel.gachas.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        //设置头部
        ToolbarUtil(binding.toolGacha).setToolHead(
            R.drawable.ic_gacha,
            getString(R.string.tool_gacha)
        )

        //滚动监听
        binding.gachaList.setScollToTopListener(binding.fabTop)

        return binding.root
    }


}