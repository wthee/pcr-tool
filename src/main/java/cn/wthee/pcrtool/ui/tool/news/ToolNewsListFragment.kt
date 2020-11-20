package cn.wthee.pcrtool.ui.tool.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.adapters.NewsListAdapter
import cn.wthee.pcrtool.data.MyAPIRepository
import cn.wthee.pcrtool.data.model.NewsData
import cn.wthee.pcrtool.databinding.FragmentToolNewsListBinding
import cn.wthee.pcrtool.utils.ToastUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ToolNewsListFragment : Fragment() {

    private val REGION = "region"
    private var region = 2
    private lateinit var binding: FragmentToolNewsListBinding

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
        val adapter = NewsListAdapter(parentFragmentManager, region)
        binding.newsList.adapter = adapter
        //获取数据
        MyAPIRepository.getNewsCall(region, 1).enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>, response: Response<NewsData>) {
                val responseBody = response.body()
                if (responseBody == null || responseBody.status != 0 || responseBody.data.isEmpty()) {
                    ToastUtil.short("未正常获取数据，请重新查询~")
                } else {
                    adapter.submitList(responseBody.data)
                }
            }

            override fun onFailure(call: Call<NewsData>, t: Throwable) {
                ToastUtil.short("获取信息失败~")
            }
        })
        return binding.root
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