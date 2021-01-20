package cn.wthee.pcrtool.data.network.service


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 数据库
 */
interface DatabaseService {
    /**
     * 根据文件名 [file]下载数据库文件
     */
    @GET("{file}")
    fun getDb(@Path("file") file: String): Call<ResponseBody>

}