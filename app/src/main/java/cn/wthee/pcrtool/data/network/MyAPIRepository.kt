package cn.wthee.pcrtool.data.network

import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.model.*
import cn.wthee.pcrtool.database.getRegion
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

    private val MediaType = "application/json; charset=utf-8"

    /**
     * 查询竞技场对战信息
     * @param ids 防守方队伍角色编号
     */
    suspend fun getPVPData(ids: JsonArray): ResponseData<List<PvpResultData>> {
        val region = getRegion()
        //接口参数
        val json = JsonObject()
        json.addProperty("region", region)
        json.add("ids", ids)
        val body =
            json.toString().toRequestBody(MediaType.toMediaTypeOrNull())
        //发送请求
        try {
            val response = service.getPVPData(body)
            if (response.message == "failure" || response.data == null) {
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
     * @param page 页码
     */
    suspend fun getNews(region: Int, page: Int): ResponseData<List<NewsTable>> {
        //接口参数
        val json = JsonObject()
        json.addProperty("region", region)
        json.addProperty("page", page)
        val body =
            json.toString().toRequestBody(MediaType.toMediaTypeOrNull())

        //请求
        try {
            val response = service.getNewsData(body)
            if (response.message == "failure" || response.data == null || response.data!!.isEmpty()) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                LogReportUtil.upload(e, Constants.EXCEPTION_API + "news" + "$region/$page")
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
            json.toString().toRequestBody(MediaType.toMediaTypeOrNull())

        //请求
        try {
            val response = service.getNewsDetail(body)
            if (response.message == "failure" || response.data == null) {
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
    suspend fun getNewsOverview(): ResponseData<List<NewsTable>> {
        //请求
        try {
            val response = service.getNewsOverview()
            if (response.message == "failure" || response.data == null || response.data!!.isEmpty()) {
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
     * 最新公告
     */
    suspend fun getNewsOverviewByRegion(region: Int): ResponseData<List<NewsTable>> {
        //请求
        try {
            val json = JsonObject()
            json.addProperty("region", region)
            val body =
                json.toString().toRequestBody(MediaType.toMediaTypeOrNull())
            val response = service.getNewsOverviewByRegion(body)
            if (response.message == "failure" || response.data == null || response.data!!.isEmpty()) {
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
     * @param page 页码
     */
    suspend fun getTweet(page: Int): ResponseData<List<TweetData>> {
        //接口参数
        val json = JsonObject()
        json.addProperty("page", page)
        val body =
            json.toString().toRequestBody(MediaType.toMediaTypeOrNull())

        //请求
        try {
            val response = service.getTweetData(body)
            if (response.message == "failure" || response.data == null || response.data!!.isEmpty()) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                LogReportUtil.upload(e, Constants.EXCEPTION_API + "tweet" + "$page")
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
            if (response.message == "failure" || response.data == null || response.data!!.isEmpty()) {
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
            if (response.message == "failure" || response.data == null) {
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
     * 获取通知信息
     */
    suspend fun getNotice(): ResponseData<List<AppNotice>> {
        //请求
        try {
            val response = service.getAppNotice()
            if (response.message == "failure" || response.data == null) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                LogReportUtil.upload(e, Constants.EXCEPTION_API + "notice")
            }
        }
        return error()
    }

    /**
     * 获取应用更新通知信息
     */
    suspend fun getAppUpdateNotice(): ResponseData<Boolean> {
        //请求
        try {
            //接口参数
            val json = JsonObject()
            json.addProperty("version", BuildConfig.VERSION_CODE)
            val body =
                json.toString().toRequestBody(MediaType.toMediaTypeOrNull())

            val response = service.toUpdate(body)
            if (response.message == "failure" || response.data == null) {
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
                json.toString().toRequestBody(MediaType.toMediaTypeOrNull())

            val response = service.getEquipArea(body)
            if (response.message == "failure" || response.data == null) {
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
            val region = getRegion()
            //接口参数
            val json = JsonObject()
            json.addProperty("id", id)
            json.addProperty("region", region)
            val body =
                json.toString().toRequestBody(MediaType.toMediaTypeOrNull())

            val response = service.getStoryList(body)
            if (response.message == "failure" || response.data == null) {
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
}
