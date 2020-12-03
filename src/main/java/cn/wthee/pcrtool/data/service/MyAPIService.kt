package cn.wthee.pcrtool.data.service

import cn.wthee.pcrtool.data.model.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

//api
interface MyAPIService {

    @POST("pvp/search")
    suspend fun getPVPData(@Body body: RequestBody): PVPData


    @POST("news")
    suspend fun getNewsData(@Body body: RequestBody): NewsData

    //获取数据库版本
    @GET
    suspend fun getDbVersion(@Url url: String): DatabaseVersion

    //获取应用版本
    @GET("app.json")
    suspend fun getAppVersion(): AppRemoteVersion

    //排行信息
    @POST("leader")
    suspend fun getLeader(): LeaderboardData
}