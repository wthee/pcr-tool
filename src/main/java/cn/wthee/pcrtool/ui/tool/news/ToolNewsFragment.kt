package cn.wthee.pcrtool.ui.tool.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.NewsViewPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentToolNewsBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ResourcesUtil
import com.google.android.material.tabs.TabLayoutMediator


class ToolNewsFragment : Fragment() {

    private lateinit var binding: FragmentToolNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FabHelper.addBackFab()
        binding = FragmentToolNewsBinding.inflate(inflater, container, false)
        //数据库版本
        val databaseType = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString("change_database", "1")?.toInt() ?: 1
        //设置头部
        binding.toolNews.apply {
            toolIcon.setImageDrawable(ResourcesUtil.getDrawable(R.drawable.ic_news))
            toolTitle.text = getString(R.string.tool_news)
        }
        binding.apply {
            val adapter = NewsViewPagerAdapter(childFragmentManager, lifecycle, databaseType)
            newsPager.adapter = adapter
            newsPager.offscreenPageLimit = 2
            TabLayoutMediator(newsTabLayout, newsPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = if (databaseType == 1)
                            getString(R.string.db_cn)
                        else
                            getString(R.string.db_jp)
                    }
                    1 -> {
                        tab.text = getString(R.string.db_tw)
                    }
                    2 -> {
                        tab.text = if (databaseType == 1)
                            getString(R.string.db_jp)
                        else
                            getString(R.string.db_cn)
                    }
                }
            }.attach()
        }
        return binding.root
    }

}