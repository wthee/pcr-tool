package cn.wthee.pcrtool.data.network

import cn.wthee.pcrtool.data.db.entity.ComicData
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.model.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * 接口
 */
interface MyAPIService {

    /**
     * 竞技场查询接口
     */
    @POST("pvp/search")
    suspend fun getPVPData(@Body body: RequestBody): ResponseData<List<PvpResultData>>

    /**
     * 获取数据库版本
     */
    @GET
    suspend fun getDbVersion(@Url url: String): ResponseData<DatabaseVersion>

    /**
     * 获取消息通知
     */
    @POST("notice")
    suspend fun getAppNotice(): ResponseData<List<AppNotice>>

    /**
     * 版本更新校验
     */
    @POST("toupdate")
    suspend fun toUpdate(@Body body: RequestBody): ResponseData<Boolean>

    /**
     * 获取公告
     */
    @POST("news")
    suspend fun getNewsData(@Body body: RequestBody): ResponseData<List<NewsTable>>

    /**
     * 获取公告
     */
    @POST("news/overview")
    suspend fun getNewsOverview(): ResponseData<List<NewsTable>>

    /**
     * 获取推特
     */
    @POST("tweet")
    suspend fun getTweetData(@Body body: RequestBody): ResponseData<List<TweetData>>

    /**
     * 获取漫画信息
     */
    @POST("comic")
    suspend fun getComicData(): ResponseData<List<ComicData>>

    /**
     * 排行信息
     */
    @POST("leaders")
    suspend fun getLeader(): ResponseData<LeaderData>
}