package cn.wthee.pcrtool.data

import androidx.preference.PreferenceManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.model.News
import cn.wthee.pcrtool.data.model.Result
import cn.wthee.pcrtool.data.model.ResultData
import cn.wthee.pcrtool.data.service.MyAPIService
import cn.wthee.pcrtool.data.view.getIds
import cn.wthee.pcrtool.enums.Response
import cn.wthee.pcrtool.ui.tool.pvp.ToolPvpFragment
import cn.wthee.pcrtool.utils.ApiHelper
import cn.wthee.pcrtool.utils.Constants
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType
import okhttp3.RequestBody

object MyAPIRepository {

    //创建服务
    private val service = ApiHelper.create(MyAPIService::class.java, Constants.MY_API_URL)

    suspend fun getPVPData(): ResultData<List<Result>> {
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
        try {
            val response = service.getPVPData(body)
            if (response.code != 0 || response.data.result == null) {
                return ResultData(Response.FAILURE, arrayListOf(), "未正常获取数据，请重新查询~")
            }
            return ResultData(Response.SUCCESS, response.data.result)
        } catch (e: Exception) {
            if (e is CancellationException) {
                return ResultData(Response.CANCEL, arrayListOf())
            }
        }
        return ResultData(Response.FAILURE, arrayListOf(), "查询失败，请重新查询~")

    }

    //官网信息
    suspend fun getNews(region: Int, page: Int): ResultData<List<News>> {
        //接口参数
        val json = JsonObject()
        json.addProperty("region", region)
        json.addProperty("page", page)
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
        //请求
        try {
            val response = service.getNewsData(body)
            if (response.status != 0 || response.data.isEmpty()) {
                return ResultData(Response.FAILURE, arrayListOf(), "未正常获取数据，请重新查询~")
            }
            return ResultData(Response.SUCCESS, response.data)
        } catch (e: Exception) {
            if (e is CancellationException) {
                return ResultData(Response.CANCEL, arrayListOf())
            }
        }
        return ResultData(Response.FAILURE, arrayListOf(), "获取数据失败，请稍后重新查询~")
    }

}

interface OnPostListener {

    fun success(data: List<Result>)

    fun error()
}