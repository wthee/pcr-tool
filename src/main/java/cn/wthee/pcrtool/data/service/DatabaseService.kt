package cn.wthee.pcrtool.data.service


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

//下载数据库
interface DatabaseService {
    //下载数据库文件
    @GET("{file}")
    fun getDb(@Path("file") file: String): Call<ResponseBody>

}