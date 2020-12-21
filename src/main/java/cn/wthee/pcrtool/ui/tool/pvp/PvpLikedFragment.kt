package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.PvpLikedAdapter
import cn.wthee.pcrtool.databinding.FragmentPvpLikedBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarUtil


class PvpLikedFragment : Fragment() {

    private lateinit var binding: FragmentPvpLikedBinding

    private val viewModel =
        InjectorUtil.providePvpViewModelFactory().create(PvpLikedViewModel::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab(2)
        binding = FragmentPvpLikedBinding.inflate(inflater, container, false)
        val type = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
            .getString("change_database", "1")?.toInt() ?: 1
        viewModel.getLiked(if (type == 1) 2 else 4)
        val adapter = PvpLikedAdapter(requireActivity())
        binding.listLiked.adapter = adapter
        //数据监听
        viewModel.data.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        //设置头部
        ToolbarUtil(binding.toolPvpLiked).setToolHead(
            R.drawable.ic_pvp,
            getString(R.string.tool_pvp_liked)
        )
        return binding.root
    }


}