package cn.wthee.pcrtool.data.service

import cn.wthee.pcrtool.data.model.PVPData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PVPService {

    @GET("search")
    fun getData(@Query("region") region: Int, @Query("ids") ids: String): Call<PVPData>
}