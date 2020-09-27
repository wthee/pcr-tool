package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.PvpCharacterPageAdapter
import cn.wthee.pcrtool.adapters.PvpCharactertAdapter
import cn.wthee.pcrtool.data.model.PVPData
import cn.wthee.pcrtool.data.service.PVPService
import cn.wthee.pcrtool.database.view.PvpCharacterData
import cn.wthee.pcrtool.database.view.getDefault
import cn.wthee.pcrtool.database.view.getIds
import cn.wthee.pcrtool.databinding.FragmentToolPvpBinding
import cn.wthee.pcrtool.utils.ApiHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ToastUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ToolPvpFragment : Fragment() {
    companion object {
        var selects = getDefault()
        lateinit var pvpCharactertAdapter: PvpCharactertAdapter
    }

    private lateinit var binding: FragmentToolPvpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolPvpBinding.inflate(inflater, container, false)
        //已选择角色
        loadDefault()
        //角色页面 绑定tab viewpager
        binding.pvpPager.postDelayed({
            setPager()
        }, resources.getInteger(R.integer.delay).toLong())
        //查询
        doSearch()
        return binding.root
    }

    private fun doSearch() {
        binding.pvpSearch.setOnClickListener {
            //参数校验
            if (selects.contains(PvpCharacterData(0, 999))) {
                ToastUtil.short("请选择 5 名角色~")
                return@setOnClickListener
            }
            //创建服务
            val service = ApiHelper.create(PVPService::class.java, Constants.API_URL_PVP)
            //接口参数
            val json = JsonObject()
            json.addProperty("region", 1)
            json.add("ids", selects.getIds())
            val body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
            );
            //发送请求
            service.getData(body).enqueue(object : Callback<PVPData> {
                override fun onResponse(call: Call<PVPData>, response: Response<PVPData>) {
                    try {
                        val body = response.body()
                        if (body == null || body.code != 0) {
                            ToastUtil.short("查询异常，请稍后重试~")
                        } else {
                            //展示查询结果
                            ToolPvpResultDialogFragment.newInstance(body.data)
                                .show(parentFragmentManager, "pvp")
                        }
                    } catch (e: Exception) {
                        ToastUtil.short("数据解析失败~")
                    }
                }

                override fun onFailure(call: Call<PVPData>, t: Throwable) {
                    ToastUtil.short("查询失败，请检查网络~")
                }
            })
        }
    }

    private fun setPager() {
        binding.pvpPager.offscreenPageLimit = 2
        binding.pvpPager.adapter = PvpCharacterPageAdapter(requireActivity())
        TabLayoutMediator(
            binding.tablayoutPosition,
            binding.pvpPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.position_1)
                }
                1 -> {
                    tab.text = getString(R.string.position_2)
                }
                2 -> {
                    tab.text = getString(R.string.position_3)
                }
            }
        }.attach()
    }

    //已选择角色
    private fun loadDefault() {
        pvpCharactertAdapter = PvpCharactertAdapter()
        binding.selectCharacters.adapter = pvpCharactertAdapter
        pvpCharactertAdapter.submitList(selects)
        pvpCharactertAdapter.notifyDataSetChanged()
    }

}