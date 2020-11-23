package cn.wthee.pcrtool.ui.tool.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.NewsAdapter
import cn.wthee.pcrtool.databinding.FragmentToolNewsBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ResourcesUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ToolNewsFragment : Fragment() {

    private lateinit var binding: FragmentToolNewsBinding
    private var page = 1
    private var region = 2
    private val newsViewModel by activityViewModels<NewsViewModel>()
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FabHelper.addBackFab()
        binding = FragmentToolNewsBinding.inflate(inflater, container, false)
        //设置头部
        binding.toolNews.apply {
            toolIcon.setImageDrawable(ResourcesUtil.getDrawable(R.drawable.ic_news))
            toolTitle.text = getString(R.string.tool_news)
        }
        //切换来源
        binding.regionChips.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chip_cn -> region = 2
                R.id.chip_tw -> region = 3
                R.id.chip_jp -> region = 4
            }
            newsViewModel.getNews(region)
        }
        //下拉刷新
        binding.refresh.apply {
            setProgressBackgroundColorSchemeColor(ResourcesUtil.getColor(R.color.colorWhite))
            setColorSchemeResources(R.color.colorPrimary)
            setOnRefreshListener {
//                getNews(adapter)
            }
        }
        //初次获取
        newsViewModel.getNews(region)
        //新闻数据更新
        newsViewModel.update.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                @OptIn(ExperimentalCoroutinesApi::class)
                newsViewModel.news.collectLatest {
                    adapter = NewsAdapter(parentFragmentManager, region)
                    binding.newsList.adapter = adapter
                    adapter.submitData(it)
                    binding.refresh.isRefreshing = false
                }

            }
        })
        //加载更多进度显示
        newsViewModel.loadingMore.observe(viewLifecycleOwner, {
//            binding.loadingMore.visibility = if (it) View.VISIBLE else View.GONE
        })
        return binding.root
    }

}