package cn.wthee.pcrtool.data.service

import cn.wthee.pcrtool.data.model.NewsData
import cn.wthee.pcrtool.data.model.PVPData
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface MyAPIService {

    @POST("pvp/search")
    suspend fun getPVPData(@Body body: RequestBody): PVPData


    @POST("news")
    suspend fun getNewsData(@Body body: RequestBody): NewsData

}