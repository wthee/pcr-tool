package cn.wthee.pcrtool.data.network

import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.view.getIds
import cn.wthee.pcrtool.data.network.model.*
import cn.wthee.pcrtool.data.network.service.MyAPIService
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.tool.pvp.PvpFragment
import cn.wthee.pcrtool.utils.ApiHelper
import cn.wthee.pcrtool.utils.Constants
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType
import okhttp3.RequestBody

object MyAPIRepository {

    //创建服务
    private val service = ApiHelper.create(MyAPIService::class.java, Constants.API_URL)

    suspend fun getPVPData(): ResponseData<List<PvpData>> {
        //接口参数
        val json = JsonObject()
        json.addProperty("region", DatabaseUpdater.getRegion())
        json.add("ids", PvpFragment.selects.getIds())
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
        //发送请求
        try {
            val response = service.getPVPData(body)
            if (response.message == "failure" || response.data == null) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            }
        }
        return error()

    }

    //官网信息
    suspend fun getNews(region: Int, page: Int): ResponseData<List<NewsTable>> {
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
            if (response.message == "failure" || response.data == null || response.data!!.isEmpty()) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            }
        }
        return error()
    }

    //排名信息
    suspend fun getLeader(): ResponseData<List<LeaderboardData>> {
        //请求
        try {
            val response = service.getLeader()
            if (response.message == "failure" || response.data == null || response.data!!.isEmpty()) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            }
        }
        return error()
    }


    //日历信息
    suspend fun getCalendar(): ResponseData<CalendarData> {
        //请求
        try {
            val response = service.getCalendar()
            if (response.message == "failure" || response.data == null) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            }
        }
        return error()
    }


}
