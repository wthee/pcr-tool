package cn.wthee.pcrtool.ui.tool.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.NewsAdapter
import cn.wthee.pcrtool.adapter.load.LoaderStateAdapter
import cn.wthee.pcrtool.databinding.FragmentToolNewsListBinding
import cn.wthee.pcrtool.utils.Constants.REGION
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.ShareIntentUtil
import cn.wthee.pcrtool.utils.ToastUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 公告列表
 */
class ToolNewsListFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolNewsListBinding.inflate(inflater, container, false)
        adapter = NewsAdapter(parentFragmentManager, region, binding.fabCopy)
        val loaderStateAdapter = LoaderStateAdapter { adapter.retry() }
        binding.newsList.adapter = adapter.withLoadStateFooter(loaderStateAdapter)

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
        //新闻数据
        loadNews()
        return binding.root
    }

    private fun loadNews() {
        binding.loading.loadingTip.text = getString(R.string.loading_news)
        binding.loading.loadingTip.setTextColor(ResourcesUtil.getColor(R.color.colorWhite))
        lifecycleScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            when (region) {
                2 -> {
                    newsViewModel.getNewsCN().collectLatest {
                        adapter.submitData(it)
                        binding.loading.root.visibility = View.GONE
                    }
                }
                3 -> {
                    newsViewModel.getNewsTW().collectLatest {
                        adapter.submitData(it)
                        binding.loading.root.visibility = View.GONE
                    }
                }
                4 -> {
                    newsViewModel.getNewsJP().collectLatest {
                        adapter.submitData(it)
                        binding.loading.root.visibility = View.GONE
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(region: Int) =
            ToolNewsListFragment().apply {
                arguments = Bundle().apply {
                    putInt(REGION, region)
                }
            }
    }
}