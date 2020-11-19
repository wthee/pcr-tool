package cn.wthee.pcrtool.ui.tool.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.data.MyAPIRepository
import cn.wthee.pcrtool.data.model.NewsData
import cn.wthee.pcrtool.databinding.FragmentToolNewsBinding
import cn.wthee.pcrtool.utils.ToastUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ToolNewsFragment : Fragment() {

    private lateinit var binding: FragmentToolNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolNewsBinding.inflate(inflater, container, false)
        MyAPIRepository.getNewsCall(2, 1).enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>, response: Response<NewsData>) {
                //TODO 加载列表
            }

            override fun onFailure(call: Call<NewsData>, t: Throwable) {
                ToastUtil.short("获取信息失败~")
            }
        })
        return binding.root
    }

}