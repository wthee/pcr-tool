package cn.wthee.pcrtool.ui.tool.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.viewpager.NewsListPagerAdapter
import cn.wthee.pcrtool.databinding.FragmentToolNewsBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToolbarHelper
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

/**
 * 官网公告 ViewPager
 *
 * 页面布局 [FragmentToolNewsBinding]
 *
 * ViewModels []
 */
@AndroidEntryPoint
class NewsPagerFragment : Fragment() {

    private lateinit var binding: FragmentToolNewsBinding
    private lateinit var adapter: NewsListPagerAdapter

    companion object {
        lateinit var viewPager: ViewPager2
        var currentPage = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolNewsBinding.inflate(inflater, container, false)
        //设置头部
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_news,
            getString(R.string.tool_news)
        )
        //viewpager
        viewPager = binding.toolList
        viewPager.offscreenPageLimit = 1
        if (viewPager.adapter == null) {
            adapter = NewsListPagerAdapter(requireActivity())
            viewPager.adapter = adapter
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
            }
        })

        TabLayoutMediator(binding.tabs, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.db_cn)
                    tab.view.setOnClickListener {
                        if (currentPage == position) {
                            val view = adapter.mFragments[position].view
                            view?.findViewById<RecyclerView>(R.id.news_list)
                                ?.smoothScrollToPosition(0)
                        }
                    }
                }
                1 -> {
                    tab.text = getString(R.string.db_tw)
                    tab.view.setOnClickListener {
                        if (currentPage == position) {
                            val view = adapter.mFragments[position].view
                            view?.findViewById<RecyclerView>(R.id.news_list)
                                ?.smoothScrollToPosition(0)
                        }
                    }
                }
                2 -> {
                    tab.text = getString(R.string.db_jp)
                    tab.view.setOnClickListener {
                        if (currentPage == position) {
                            val view = adapter.mFragments[position].view
                            view?.findViewById<RecyclerView>(R.id.news_list)
                                ?.smoothScrollToPosition(0)
                        }
                    }
                }
            }
        }.attach()

        return binding.root
    }
}