package cn.wthee.pcrtool.data.service


import cn.wthee.pcrtool.data.model.DatabaseVersion
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface DatabaseService {
    //下载数据库文件
    @GET("db/{file}")
    fun getDb(@Path("file") file: String): Call<ResponseBody>

    //获取数据库版本https://redive.estertion.win/last_version_cn.json
    @GET
    fun getDbVersion(@Url url: String): Call<DatabaseVersion>
}