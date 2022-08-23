package cn.wthee.pcrtool.data.network

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
     * 版本更新内容
     */
    @POST("toupdate/content")
    suspend fun getUpdateContent(@Body body: RequestBody): ResponseData<AppNotice>

    /**
     * 获取公告
     */
    @POST("news/v2")
    suspend fun getNewsData(@Body body: RequestBody): ResponseData<List<NewsTable>>

    /**
     * 获取公告
     */
    @POST("news/overview/region")
    suspend fun getNewsOverviewByRegion(@Body body: RequestBody): ResponseData<List<NewsTable>>

    /**
     * 获取公告
     */
    @POST("news/detail")
    suspend fun getNewsDetail(@Body body: RequestBody): ResponseData<NewsTable>

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

    /**
     * 查询额外装备掉落信息
     */
    @POST("equip/area")
    suspend fun getEquipArea(@Body body: RequestBody): ResponseData<List<RandomEquipDropArea>>


    /**
     * 剧情立绘
     */
    @POST("story/list")
    suspend fun getStoryList(@Body body: RequestBody): ResponseData<String>

}