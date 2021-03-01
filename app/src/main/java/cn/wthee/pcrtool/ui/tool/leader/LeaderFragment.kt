package cn.wthee.pcrtool.ui.tool.leader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CharacterLeaderAdapter
import cn.wthee.pcrtool.databinding.FragmentToolLeaderBinding
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarHelper

/**
 * 角色排行
 *
 * 页面布局 [FragmentToolLeaderBinding]
 *
 * ViewModels [LeaderViewModel]
 */
class LeaderFragment : Fragment() {

    private lateinit var binding: FragmentToolLeaderBinding
    private val leaderViewModel by activityViewModels<LeaderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolLeaderBinding.inflate(inflater, container, false)
        leaderViewModel.getLeader()
        leaderViewModel.leaderData.observe(viewLifecycleOwner) {
            if (it.status == 0) {
                binding.tip.apply {
                    text = it.data?.desc?.replace("\n", " ")
                    isSelected = true
                }
                val adapter = CharacterLeaderAdapter(requireContext())
                binding.toolList.adapter = adapter
                adapter.submitList(it.data?.leader) {
                    binding.loading.visibility = View.GONE
                }
            } else if (it.status == -1) {
                ToastUtil.short(it.message)
            }
        }

        //设置头部
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_leader,
            getString(R.string.tool_leader)
        )
        //来源
        binding.source.setOnClickListener {
            BrowserUtil.open(requireContext(), getString(R.string.leader_source_url))
        }
        return binding.root
    }

}