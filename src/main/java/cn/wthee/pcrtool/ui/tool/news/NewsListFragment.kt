package cn.wthee.pcrtool.ui.tool.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.NewsAdapter
import cn.wthee.pcrtool.adapter.load.LoaderStateAdapter
import cn.wthee.pcrtool.databinding.FragmentToolNewsListBinding
import cn.wthee.pcrtool.utils.Constants.REGION
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.ShareIntentUtil
import cn.wthee.pcrtool.utils.ToastUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 公告列表
 *
 * 页面布局 [FragmentToolNewsListBinding]
 *
 * ViewModels [NewsViewModel]
 */
class NewsListFragment : Fragment() {

    companion object {
        fun newInstance(region: Int) =
            NewsListFragment().apply {
                arguments = Bundle().apply {
                    putInt(REGION, region)
                }
            }
    }

    private var region = 0
    private lateinit var binding: FragmentToolNewsListBinding
    private val newsViewModel by activityViewModels<NewsViewModel>()
    private lateinit var adapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            region = getInt(REGION)
        }
    }

    @ExperimentalPagingApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolNewsListBinding.inflate(inflater, container, false)
        adapter = NewsAdapter(parentFragmentManager, region, binding.fabCopy)
        val loaderStateAdapter = LoaderStateAdapter { adapter.retry() }
        binding.newsList.adapter = adapter.withLoadStateFooter(loaderStateAdapter)
        setListener()
        //新闻数据
        loadNews()
        return binding.root
    }

    @ExperimentalPagingApi
    private fun setListener() {
        //下拉刷新
        binding.refresh.apply {
            setProgressBackgroundColorSchemeColor(ResourcesUtil.getColor(R.color.colorWhite))
            setColorSchemeResources(R.color.colorPrimary)
            setOnRefreshListener {
                loadNews()
                isRefreshing = false
            }
        }
        //复制
        binding.fabCopy.hide()
        binding.fabCopy.setOnClickListener {
            val selecteds = adapter.getSelected()
            if (selecteds.isNotEmpty()) {
                var contents = ""
                selecteds.forEach {
                    contents += it.content
                }
                contents += getString(R.string.from, getString(R.string.app_name))
                ShareIntentUtil.text(contents)
            } else {
                ToastUtil.short("未选择公告~")
            }
        }
    }

    @ExperimentalPagingApi
    private fun loadNews() {
        lifecycleScope.launch {
            when (region) {
                2 -> {
                    newsViewModel.getNewsCN().collectLatest {
                        adapter.submitData(it)
                    }
                }
                3 -> {
                    newsViewModel.getNewsTW().collectLatest {
                        adapter.submitData(it)
                    }
                }
                4 -> {
                    newsViewModel.getNewsJP().collectLatest {
                        adapter.submitData(it)
                    }
                }
            }
        }
    }

}