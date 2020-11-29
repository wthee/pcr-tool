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
import cn.wthee.pcrtool.adapters.load.LoaderStateAdapter
import cn.wthee.pcrtool.databinding.FragmentToolNewsListBinding
import cn.wthee.pcrtool.utils.ResourcesUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val REGION = "region"

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
        adapter = NewsAdapter(parentFragmentManager, region)
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
        @JvmStatic
        fun newInstance(region: Int) =
            ToolNewsListFragment().apply {
                arguments = Bundle().apply {
                    putInt(REGION, region)
                }
            }
    }
}