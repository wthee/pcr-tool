package cn.wthee.pcrtool.data.service


import cn.wthee.pcrtool.data.model.DatabaseVersion
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DatabaseService {
    //下载数据库文件
    @GET("db/{file}")
    fun getDb(@Path("file") file: String): Call<ResponseBody>

    //获取数据库版本https://redive.estertion.win/last_version_cn.json
    @GET("last_version_cn.json")
    fun getDbVersion(): Call<DatabaseVersion>
}