package cn.wthee.pcrtool.data

import androidx.preference.PreferenceManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.model.PVPData
import cn.wthee.pcrtool.data.model.Result
import cn.wthee.pcrtool.data.service.PVPService
import cn.wthee.pcrtool.database.view.getIds
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

object PvpDataRepository {

    fun getData(onPostListener: OnPostListener){
        //创建服务
        val service = ApiHelper.create(PVPService::class.java, Constants.API_URL_PVP)
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
        service.getData(body).enqueue(object : Callback<PVPData> {
            override fun onResponse(call: Call<PVPData>, response: Response<PVPData>) {
                try {
                    val responseBody = response.body()
                    if (responseBody == null || responseBody.code != 0) {
                        ToastUtil.short("查询异常，请稍后重试~")
                    } else {
                        onPostListener.success(responseBody.data.result)
                    }
                } catch (e: Exception) {
                    ToastUtil.short("数据解析失败~")
                }
            }

            override fun onFailure(call: Call<PVPData>, t: Throwable) {
                onPostListener.error()
            }
        })
    }


}
interface OnPostListener{

    fun success(data: List<Result>)

    fun error()
}