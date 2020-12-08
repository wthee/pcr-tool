package cn.wthee.pcrtool.data

import androidx.preference.PreferenceManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.model.*
import cn.wthee.pcrtool.data.service.MyAPIService
import cn.wthee.pcrtool.data.view.getIds
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
        val databaseType = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
            .getString("change_database", "1")?.toInt() ?: 1
        val region = if (databaseType == 1) 2 else 4
        json.addProperty("region", region)
        json.add("ids", PvpFragment.selects.getIds())
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
        //发送请求
        try {
            val response = service.getPVPData(body)
            if (response.status != 0 || response.data == null) {
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
    suspend fun getNews(region: Int, page: Int): ResponseData<List<NewsData>> {
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
            if (response.status != 0 || response.data == null || response.data!!.isEmpty()) {
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
            if (response.status != 0 || response.data == null || response.data!!.isEmpty()) {
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
    suspend fun getCalendar(year: Int, month: Int, day: Int): ResponseData<List<CalendarData>> {
        //请求
        try {
            //接口参数
            val json = JsonObject()
            json.addProperty("year", year)
            json.addProperty("month", month)
            json.addProperty("day", day)
            val body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
            )
            val response = service.getCalendar(body)
            if (response.status != 0 || response.data == null) {
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
