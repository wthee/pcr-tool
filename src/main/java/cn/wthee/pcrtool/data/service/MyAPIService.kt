package cn.wthee.pcrtool.data.service

import cn.wthee.pcrtool.data.model.NewsData
import cn.wthee.pcrtool.data.model.PVPData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MyAPIService {

    @POST("pvp/search")
    fun getPVPData(@Body body: RequestBody): Call<PVPData>


    @POST("news")
    fun getNewsData(@Body body: RequestBody): Call<NewsData>

}