package cn.wthee.pcrtool.data.network

import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.model.*
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.utils.Constants
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

/**
 * 接口 Repository
 *
 * 数据来源 [MyAPIService]
 */
class MyAPIRepository @Inject constructor(private val service: MyAPIService) {

    /**
     * 根据防守方 id [ids] 查询竞技场对战信息
     */
    suspend fun getPVPData(ids: JsonArray): ResponseData<List<PvpResultData>> {
        val region = getRegion()
        //接口参数
        val json = JsonObject()
        json.addProperty("region", region)
        json.add("ids", ids)
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
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
                    UMCrash.generateCustomLog(e, Constants.EXCEPTION_API)
                }
            }
        }
        return error()

    }

    /**
     * 根据游戏版本 [region] 页数 [page]，查询公告信息
     */
    suspend fun getNews(region: Int, page: Int): ResponseData<List<NewsTable>> {
        //接口参数
        val json = JsonObject()
        json.addProperty("region", region)
        json.addProperty("page", page)
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
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
                MainScope().launch {
                    UMCrash.generateCustomLog(e, Constants.EXCEPTION_API)
                }
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
                MainScope().launch {
                    UMCrash.generateCustomLog(e, Constants.EXCEPTION_API)
                }
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
                MainScope().launch {
                    UMCrash.generateCustomLog(e, Constants.EXCEPTION_API)
                }
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
            val body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json.toString()
            )
            val response = service.toUpdate(body)
            if (response.message == "failure" || response.data == null) {
                return error()
            }
            return response
        } catch (e: Exception) {
            if (e is CancellationException) {
                return cancel()
            } else {
                MainScope().launch {
                    UMCrash.generateCustomLog(e, Constants.EXCEPTION_API)
                }
            }
        }
        return error()
    }
}
