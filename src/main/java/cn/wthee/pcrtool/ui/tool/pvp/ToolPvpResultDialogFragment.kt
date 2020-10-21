package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.PvpCharacterResultAdapter
import cn.wthee.pcrtool.data.model.PVPData
import cn.wthee.pcrtool.data.service.PVPService
import cn.wthee.pcrtool.database.view.getIds
import cn.wthee.pcrtool.databinding.FragmentToolPvpResultBinding
import cn.wthee.pcrtool.utils.ApiHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


const val RESULT = "results"

class ToolPvpResultDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentToolPvpResultBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolPvpResultBinding.inflate(inflater, container, false)
        //参数校验

        //创建服务
        val service = ApiHelper.create(PVPService::class.java, Constants.API_URL_PVP)
        //接口参数
        val json = JsonObject()
        val databaseType = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString("change_database", "1")?.toInt() ?: 1
        val region = if (databaseType == 1) 2 else 4
        json.addProperty("region", region)
        json.add("ids", ToolPvpFragment.selects.getIds())
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        );
        //发送请求
        service.getData(body).enqueue(object : Callback<PVPData> {
            override fun onResponse(call: Call<PVPData>, response: Response<PVPData>) {
                try {
                    val responseBody = response.body()
                    if (responseBody == null || responseBody.code != 0) {
                        ToastUtil.short("查询异常，请稍后重试~")
                    } else {
                        //展示查询结果
                        if (responseBody.data.result.isEmpty()) {
                            binding.pvpNoData.visibility = View.VISIBLE
                        } else {
                            binding.pvpNoData.visibility = View.GONE
                            val adapter = PvpCharacterResultAdapter()
                            binding.list.adapter = adapter
                            adapter.submitList(responseBody.data.result)
                        }
                    }
                } catch (e: Exception) {
                    ToastUtil.short("数据解析失败~")
                }
                binding.pvpResultLoading.visibility = View.GONE
            }

            override fun onFailure(call: Call<PVPData>, t: Throwable) {
                ToastUtil.short("查询失败，请检查网络~")
            }
        })

        //toolbar
        val toolbar = ToolbarUtil(binding.pvpResultToolbar)
        toolbar.title.text = "进攻方信息"
        toolbar.hideRightIcon()
        toolbar.setLeftIcon(R.drawable.ic_back)
        toolbar.setRightIcon(R.drawable.ic_detail_share)
        toolbar.setCenterStyle()
        toolbar.leftIcon.setOnClickListener {
            dialog?.dismiss()
        }
        return binding.root
    }

}