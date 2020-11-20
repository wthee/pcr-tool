package cn.wthee.pcrtool.data.service

import cn.wthee.pcrtool.data.model.BilibiliNewsData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface BilibiliNewsService {
    @GET("{id}")
    fun getNewsData(@Path("id") detailId: Int): Call<BilibiliNewsData>
}