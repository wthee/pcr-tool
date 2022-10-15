package cn.wthee.pcrtool.data.network

import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.model.*
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * 接口 Repository
 *
 * @param service 接口服务
 */
class MyAPIRepository @Inject constructor(private val service: MyAPIService) {

    private val mediaType = "application/json; charset=utf-8"

    /**
     * 查询竞技场对战信息
     * @param ids 防守方队伍角色编号
     */
    suspend fun getPVPData(ids: JsonArray): ResponseData<List<PvpResultData>> {
        //接口参数
        val json = JsonObject()
        json.addProperty("region", MainActivity.regionType)
        json.add("ids", ids)
        val body =
            json.toString().toRequestBody(mediaType.toMediaTypeOrNull())
        //发送请求
        try {
            val response = service.getPVPData(body)
            if (isError(response)) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                MainScope().launch {
                    LogReportUtil.upload(e, Constants.EXCEPTION_API + "pvp" + ids)
                }
            }
        }
        return error()

    }

    /**
     * 查询公告信息
     * @param region 区服 2：b服，3：台服，4：日服
     * @param after 查询该id前的数据
     * @param keyword 关键词
     */
    suspend fun getNews(region: Int, after: Int?, keyword: String): ResponseData<List<NewsTable>> {
        //接口参数
        val json = JsonObject()
        json.addProperty("region", region)
        json.addProperty("after", after)
        json.addProperty("keyword", keyword)
        val body =
            json.toString().toRequestBody(mediaType.toMediaTypeOrNull())

        //请求
        try {
            val response = service.getNewsData(body)
            if (isError(response)) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                LogReportUtil.upload(e, Constants.EXCEPTION_API + "news" + "$region/$after")
            }
        }
        return error()
    }

    /**
     * 查询公告信息
     * @param id
     */
    suspend fun getNewsDetail(id: String): ResponseData<NewsTable> {
        //接口参数
        val json = JsonObject()
        json.addProperty("id", id)
        val body =
            json.toString().toRequestBody(mediaType.toMediaTypeOrNull())

        //请求
        try {
            val response = service.getNewsDetail(body)
            if (isError(response)) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                LogReportUtil.upload(e, Constants.EXCEPTION_API + "news" + id)
            }
        }
        return error()
    }

    /**
     * 最新公告
     */
    suspend fun getNewsOverviewByRegion(region: Int): ResponseData<List<NewsTable>> {
        //请求
        try {
            val json = JsonObject()
            json.addProperty("region", region)
            val body =
                json.toString().toRequestBody(mediaType.toMediaTypeOrNull())
            val response = service.getNewsOverviewByRegion(body)
            if (isError(response)) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                LogReportUtil.upload(e, Constants.EXCEPTION_API + "newsoverview")
            }
        }
        return error()
    }

    /**
     * 查询推特信息
     * @param after 查询该id前的数据
     * @param keyword 关键词
     */
    suspend fun getTweet(after: Int?, keyword: String): ResponseData<List<TweetData>> {
        //接口参数
        val json = JsonObject()
        json.addProperty("keyword", keyword)
        json.addProperty("after", after)
        val body =
            json.toString().toRequestBody(mediaType.toMediaTypeOrNull())

        //请求
        try {
            val response = service.getTweetData(body)
            if (isError(response)) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                LogReportUtil.upload(e, Constants.EXCEPTION_API + "tweet" + "$after")
            }
        }
        return error()
    }

    /**
     * 查询漫画信息
     */
    suspend fun getComic(): ResponseData<List<ComicData>> {
        //请求
        try {
            val response = service.getComicData()
            if (isError(response)) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                LogReportUtil.upload(e, Constants.EXCEPTION_API + "tweet")
            }
        }
        return error()
    }

    /**
     * 获取排名信息
     */
    suspend fun getLeader(): ResponseData<LeaderData> {
        //请求
        try {
            val response = service.getLeader()
            if (isError(response)) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                LogReportUtil.upload(e, Constants.EXCEPTION_API + "leader")
            }
        }
        return error()
    }

    /**
     * 获取应用更新通知详情
     */
    suspend fun getUpdateContent(): ResponseData<AppNotice> {
        //请求
        try {
            //接口参数
            val json = JsonObject()
            //测试版本显示更新布局
            json.addProperty("version", BuildConfig.VERSION_CODE)
            val body =
                json.toString().toRequestBody(mediaType.toMediaTypeOrNull())

            val response = service.getUpdateContent(body)
            if (isError(response)) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                LogReportUtil.upload(e, Constants.EXCEPTION_API + "update")
            }
        }
        return error()
    }

    /**
     * 查询额外装备掉落信息
     */
    suspend fun getEquipArea(equipId: Int): ResponseData<List<RandomEquipDropArea>> {
        //请求
        try {
            //接口参数
            val json = JsonObject()
            json.addProperty("equipId", equipId)
            val body =
                json.toString().toRequestBody(mediaType.toMediaTypeOrNull())

            val response = service.getEquipArea(body)
            if (isError(response)) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                LogReportUtil.upload(e, Constants.EXCEPTION_API + "update")
            }
        }
        return error()
    }

    /**
     * 查询剧情立绘信息
     */
    suspend fun getStoryList(id: Int): ResponseData<String> {
        //请求
        try {
            //接口参数
            val json = JsonObject()
            json.addProperty("id", id)
            json.addProperty("region", MainActivity.regionType)
            val body =
                json.toString().toRequestBody(mediaType.toMediaTypeOrNull())

            val response = service.getStoryList(body)
            if (isError(response)) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                LogReportUtil.upload(e, Constants.EXCEPTION_API + "update")
            }
        }
        return error()
    }

    private
    fun <T> isError(response: ResponseData<T>): Boolean {
        if (response.message == "failure" || response.data == null) {
            return true
        } else if (response.data is List<*>) {
            return (response.data as List<*>).isEmpty()
        } else {
            return false
        }
    }

}
