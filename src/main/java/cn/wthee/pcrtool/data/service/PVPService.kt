package cn.wthee.pcrtool.data.service

import cn.wthee.pcrtool.data.model.PVPData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PVPService {

    @POST("search")
    fun getData(@Body() body: RequestBody): Call<PVPData>
}