package cn.wthee.pcrtool.data

import android.util.Log
import androidx.preference.PreferenceManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.model.BilibiliNewsData
import cn.wthee.pcrtool.data.model.NewsData
import cn.wthee.pcrtool.data.model.PVPData
import cn.wthee.pcrtool.data.model.Result
import cn.wthee.pcrtool.data.service.BilibiliNewsService
import cn.wthee.pcrtool.data.service.MyAPIService
import cn.wthee.pcrtool.data.view.getIds
import cn.wthee.pcrtool.ui.tool.pvp.ToolPvpFragment
import cn.wthee.pcrtool.utils.ApiHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ToastUtil
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MyAPIRepository {

    //创建服务
    private val service = ApiHelper.create(MyAPIService::class.java, Constants.MY_API_URL)

    fun getPVPData(onPostListener: OnPostListener): Call<PVPData> {
        //接口参数
        val json = JsonObject()
        val databaseType = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
            .getString("change_database", "1")?.toInt() ?: 1
        val region = if (databaseType == 1) 2 else 4
        json.addProperty("region", region)
        json.add("ids", ToolPvpFragment.selects.getIds())
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
        //发送请求
        val request = service.getPVPData(body)
        request.enqueue(object : Callback<PVPData> {
            override fun onResponse(call: Call<PVPData>, response: Response<PVPData>) {
                try {
                    val responseBody = response.body()
                    if (responseBody == null || responseBody.code != 0 || responseBody.data.result == null) {
                        ToastUtil.short("未正常获取数据，请重新查询~")
                    } else {
                        onPostListener.success(responseBody.data.result)
                    }
                } catch (e: Exception) {
                    ToastUtil.short("未正常解析数据，请重新查询~")
                }
            }

            override fun onFailure(call: Call<PVPData>, t: Throwable) {
                Log.e("api-failure", t.message ?: "")
                ToastUtil.short("查询失败，请检查网络~")
                onPostListener.error()
            }
        })
        return request
    }

    //官网信息
    fun getNewsCall(region: Int, page: Int): Call<NewsData> {
        //接口参数
        val json = JsonObject()
        json.addProperty("region", region)
        json.addProperty("page", page)
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
        //请求
        return service.getNewsData(body)
    }

    //bilibili官网信息详情
    fun getNewsDetailCall(id: Int): Call<BilibiliNewsData> {
        val service = ApiHelper.create(BilibiliNewsService::class.java, Constants.BILIBILI_API_URL)
        //请求
        return service.getNewsData(id)
    }
}

interface OnPostListener {

    fun success(data: List<Result>)

    fun error()
}