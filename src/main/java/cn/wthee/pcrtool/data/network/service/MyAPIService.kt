package cn.wthee.pcrtool.data.network.service

import cn.wthee.pcrtool.data.network.model.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

//api
interface MyAPIService {

    @POST("pvp/search")
    suspend fun getPVPData(@Body body: RequestBody): ResponseData<List<PvpData>>

    //获取数据库版本
    @GET
    suspend fun getDbVersion(@Url url: String): ResponseData<DatabaseVersion>

    //获取应用版本
    @GET("app.json")
    suspend fun getAppVersion(): ResponseData<AppRemoteVersion>

    //公告
    @POST("news")
    suspend fun getNewsData(@Body body: RequestBody): ResponseData<List<NewsData>>

    //排行信息
    @POST("leader")
    suspend fun getLeader(): ResponseData<List<LeaderboardData>>

    //国服日历
    @POST("calendar")
    suspend fun getCalendar(): ResponseData<CalendarData>
}