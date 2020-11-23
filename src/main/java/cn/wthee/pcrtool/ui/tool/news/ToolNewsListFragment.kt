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
import cn.wthee.pcrtool.databinding.FragmentToolNewsListBinding
import cn.wthee.pcrtool.utils.ResourcesUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ToolNewsListFragment : Fragment() {

    private val REGION = "region"
    private var region = 2
    private lateinit var binding: FragmentToolNewsListBinding
    private var page = 1
    private val newsViewModel by activityViewModels<NewsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            region = it.getInt(REGION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolNewsListBinding.inflate(inflater, container, false)
        val adapter = NewsAdapter(parentFragmentManager, region)
        binding.newsList.adapter = adapter
        //下拉刷新
        binding.refresh.apply {
            setProgressBackgroundColorSchemeColor(ResourcesUtil.getColor(R.color.colorWhite))
            setColorSchemeResources(R.color.colorPrimary)
            setOnRefreshListener {
                getNews(adapter)
            }
        }
        newsViewModel.getNews(region)
        newsViewModel.update.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                @OptIn(ExperimentalCoroutinesApi::class)
                newsViewModel.news.collectLatest {
                    adapter.submitData(it)
                }
            }
        })
        //获取数据
        getNews(adapter)
        return binding.root
    }

    private fun getNews(adapter: NewsAdapter) {

//        MyAPIRepository.getNewsCall(region, page).enqueue(object : Callback<NewsData> {
//            override fun onResponse(call: Call<NewsData>, response: Response<NewsData>) {
//                val responseBody = response.body()
//                if (responseBody == null || responseBody.status != 0 || responseBody.data.isEmpty()) {
//                    ToastUtil.short("未正常获取数据，请重新查询~")
//                } else {
//                    adapter.submitList(responseBody.data) {
//                        binding.loading.visibility = View.GONE
//                        binding.refresh.isRefreshing = false
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<NewsData>, t: Throwable) {
//                ToastUtil.short("获取信息失败~")
//            }
//        })
    }

    companion object {
        @JvmStatic
        fun getInstance(region: Int) =
            ToolNewsListFragment().apply {
                arguments = Bundle().apply {
                    putInt(REGION, region)
                }
            }
    }
}