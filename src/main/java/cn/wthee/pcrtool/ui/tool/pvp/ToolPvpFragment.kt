package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.PvpCharacterPageAdapter
import cn.wthee.pcrtool.adapters.PvpCharactertAdapter
import cn.wthee.pcrtool.database.view.PvpCharacterData
import cn.wthee.pcrtool.database.view.getDefault
import cn.wthee.pcrtool.databinding.FragmentToolPvpBinding
import cn.wthee.pcrtool.utils.ToastUtil
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ToolPvpFragment : Fragment() {
    companion object {
        var selects = getDefault()
        lateinit var pvpCharactertAdapter: PvpCharactertAdapter
        lateinit var progressBar: ProgressBar
    }

    private lateinit var binding: FragmentToolPvpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolPvpBinding.inflate(inflater, container, false)
        progressBar = binding.pvpProgressBar
        //已选择角色
        loadDefault()
        //角色页面 绑定tab viewpager
        try {
            lifecycleScope.launch {
                delay(600L)
                setPager()
            }
        } catch (e: Exception) {
        }

        setListener()
        return binding.root
    }

    private fun setListener() {
        binding.apply {
            pvpSearch.setOnClickListener {
                if (ToolPvpFragment.selects.contains(PvpCharacterData(0, 999))) {
                    ToastUtil.short("请选择 5 名角色~")
                } else {
                    //展示查询结果
                    ToolPvpResultDialogFragment().show(parentFragmentManager, "pvp")
                }
            }
            //返回
            toolPvp.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setPager() {
        binding.pvpPager.offscreenPageLimit = 3
        binding.pvpPager.adapter = PvpCharacterPageAdapter(requireActivity())
        TabLayoutMediator(
            binding.tablayoutPosition,
            binding.pvpPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.position_1)
                }
                1 -> {
                    tab.text = getString(R.string.position_2)
                }
                2 -> {
                    tab.text = getString(R.string.position_3)
                }
            }
        }.attach()
    }

    //已选择角色
    private fun loadDefault() {
        pvpCharactertAdapter = PvpCharactertAdapter()
        binding.selectCharacters.adapter = pvpCharactertAdapter
        pvpCharactertAdapter.submitList(selects)
        pvpCharactertAdapter.notifyDataSetChanged()
    }

}