package cn.wthee.pcrtool.data.network

import cn.wthee.pcrtool.data.db.entity.ComicData
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.model.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

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
    @POST("db/info/v2")
    suspend fun getDbVersion(@Body body: RequestBody): ResponseData<DatabaseVersion>

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
     * 获取推特
     */
    @POST("tweet/from_bilibili")
    suspend fun getTweetData(@Body body: RequestBody): ResponseData<List<TweetData>>

    /**
     * 获取漫画
     */
    @POST("comic/zh")
    suspend fun getComicData(@Body body: RequestBody): ResponseData<List<ComicData>>

    /**
     * 排行信息
     */
    @POST("leaders/score")
    suspend fun getLeader(): ResponseData<List<LeaderboardData>>

    /**
     * 查询额外装备掉落信息
     */
    @POST("equip/area/v2")
    suspend fun getEquipArea(@Body body: RequestBody): ResponseData<List<RandomEquipDropArea>>

    /**
     * 剧情立绘
     */
    @POST("story/list")
    suspend fun getStoryList(@Body body: RequestBody): ResponseData<String>

    /**
     * 获取网站列表
     */
    @POST("website/list/v2")
    suspend fun getWebsiteList(): ResponseData<List<WebsiteGroupData>>

    /**
     * 排行评级信息
     */
    @POST("leaders/tier/v2")
    suspend fun getLeaderTier(@Body body: RequestBody): ResponseData<LeaderTierData>

    /**
     * 关键词
     */
    @POST("keyword")
    suspend fun getKeywords(@Body body: RequestBody): ResponseData<List<KeywordData>>
}