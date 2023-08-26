package cn.wthee.pcrtool.data.network


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 数据库
 */
interface FileService {
    /**
     * 根据文件名 [file]下载文件
     */
    @GET("{file}")
    fun getFile(@Path("file") file: String): Call<ResponseBody>

}