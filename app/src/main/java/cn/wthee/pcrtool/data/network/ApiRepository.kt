package cn.wthee.pcrtool.data.network

import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.data.db.entity.ComicData
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.data.model.DatabaseVersion
import cn.wthee.pcrtool.data.model.KeywordData
import cn.wthee.pcrtool.data.model.LeaderTierData
import cn.wthee.pcrtool.data.model.LeaderboardData
import cn.wthee.pcrtool.data.model.PvpResultData
import cn.wthee.pcrtool.data.model.RandomEquipDropArea
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.model.WebsiteGroupData
import cn.wthee.pcrtool.data.model.cancel
import cn.wthee.pcrtool.data.model.error
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.coroutines.cancellation.CancellationException

/**
 * 接口请求
 */
class ApiRepository {

    /**
     * 请求异常捕获
     */
    private suspend inline fun <reified T> postCatching(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): ResponseData<T> {
        try {
            val response = ApiClient.client.post(urlString) {
                block()
            }
            val body = response.body<ResponseData<T>>()
            if (isError(body)) {
                return error()
            }
            return body
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                MainScope().launch {
                    LogReportUtil.upload(
                        e,
                        Constants.EXCEPTION_API + urlString
                    )
                }
            }
        }
        return error()
    }

    private inline fun <reified T> isError(response: ResponseData<T>): Boolean {
        return response.message == "failure" || response.data == null
    }

    /**
     * 竞技场查询接口
     */
    suspend fun getPvpData(ids: JsonArray): ResponseData<List<PvpResultData>> =
        postCatching("pvp/search") {
            setBody(
                buildJsonObject {
                    put("region", MainActivity.regionType.value)
                    put("ids", ids)
                }
            )
        }

    /**
     * 获取数据库版本
     */
    suspend fun getDbVersion(regionCode: String): ResponseData<DatabaseVersion> =
        postCatching("db/info/v2") {
            setBody(
                buildJsonObject {
                    put("regionCode", regionCode)
                }
            )
        }

    /**
     * 版本更新内容
     */
    suspend fun getUpdateContent(): ResponseData<AppNotice> =
        postCatching("toupdate/content") {
            setBody(
                buildJsonObject {
                    put("version", BuildConfig.VERSION_CODE)
                }
            )
        }

    /**
     * 查询公告信息
     * @param region 区服 2：b服，3：台服，4：日服
     * @param after 查询该id前的数据
     * @param keyword 关键词
     * @param startTime 开始时间 格式如：2020/01/01 00:00:00
     * @param endTime 结束时间
     */
    suspend fun getNewsList(
        region: Int,
        after: Int?,
        keyword: String,
        startTime: String,
        endTime: String
    ): ResponseData<List<NewsTable>> =
        postCatching("news/v2") {
            setBody(
                buildJsonObject {
                    put("region", region)
                    put("after", after)
                    put("keyword", keyword)
                    put("startTime", startTime)
                    put("endTime", endTime)
                }
            )
        }

    /**
     * 获取公告（最新3条）
     */
    suspend fun getNewsOverviewByRegion(): ResponseData<List<NewsTable>> =
        postCatching("news/overview/region") {
            setBody(
                buildJsonObject {
                    put("region", MainActivity.regionType.value)
                }
            )
        }

    /**
     * 关键词
     * @param type 类型，[cn.wthee.pcrtool.data.enums.KeywordType]
     */
    suspend fun getKeywords(type: Int): ResponseData<List<KeywordData>> =
        postCatching("keyword") {
            setBody(
                buildJsonObject {
                    put("region", MainActivity.regionType.value)
                    put("type", type)
                }
            )
        }

    /**
     * 查询推特信息
     * @param after 查询该id前的数据
     * @param keyword 关键词
     * @param startTime 开始时间 格式如：2020/01/01 00:00:00
     * @param endTime 结束时间
     */
    suspend fun getTweetList(
        after: Int?,
        keyword: String,
        startTime: String,
        endTime: String
    ): ResponseData<List<TweetData>> =
        postCatching("tweet/from_bilibili") {
            setBody(
                buildJsonObject {
                    put("keyword", keyword)
                    put("after", after)
                    put("startTime", startTime)
                    put("endTime", endTime)
                }
            )
        }

    /**
     * 查询漫画列表
     * 参数暂不生效
     */
    suspend fun getComicList(after: Int?, keyword: String): ResponseData<List<ComicData>> =
        postCatching("comic/zh") {
            setBody(
                buildJsonObject {
                    put("keyword", keyword)
                    put("after", after)
                }
            )
        }

    /**
     * 过场漫画列表
     */
    suspend fun getLoadComicList(): ResponseData<List<String>> =
        postCatching("load/comic/list")

    /**
     * 查询过场漫画类型
     */
    suspend fun getLoadComicType(id: Int): ResponseData<String> =
        postCatching("load/comic/type") {
            setBody(
                buildJsonObject {
                    put("id", id)
                }
            )
        }

    /**
     * 剧情立绘
     */
    suspend fun getStoryList(id: Int): ResponseData<String> =
        postCatching("story/list") {
            setBody(
                buildJsonObject {
                    put("id", id)
                }
            )
        }

    /**
     * 查询额外装备掉落信息
     */
    suspend fun getEquipArea(equipId: Int): ResponseData<List<RandomEquipDropArea>> =
        postCatching("equip/area/v2") {
            setBody(
                buildJsonObject {
                    put("equipId", equipId)
                }
            )
        }

    /**
     * 获取网站列表
     */
    suspend fun getWebsiteList(): ResponseData<List<WebsiteGroupData>> =
        postCatching("website/list/v2")

    /**
     * 排行信息
     */
    suspend fun getLeader(): ResponseData<List<LeaderboardData>> =
        postCatching("leaders/score")

    /**
     * 排行评级信息
     */
    suspend fun getLeaderTier(type: Int): ResponseData<LeaderTierData> =
        postCatching("leaders/tier/v2") {
            setBody(
                buildJsonObject {
                    put("type", type)
                }
            )
        }
}
