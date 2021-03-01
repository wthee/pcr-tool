package cn.wthee.pcrtool.data.network

import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.data.entity.NewsTable
import cn.wthee.pcrtool.data.model.*
import cn.wthee.pcrtool.data.network.service.MyAPIService
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.utils.ApiUtil
import cn.wthee.pcrtool.utils.Constants
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * 接口 Repository
 *
 * 数据来源 [MyAPIService]
 */
class MyAPIRepository(private val service: MyAPIService) {

    companion object {

        @Volatile
        private var instance: MyAPIRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MyAPIRepository(
                    ApiUtil.create(
                        MyAPIService::class.java,
                        Constants.API_URL,
                        20
                    )
                ).also { instance = it }
            }
    }


    /**
     * 根据防守方 id [ids] 查询竞技场对战信息
     */
    suspend fun getPVPData(ids: JsonArray): ResponseData<List<PvpResultData>> {
        val region = DatabaseUpdater.getRegion()
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
                return cn.wthee.pcrtool.data.model.error()
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
        return cn.wthee.pcrtool.data.model.error()

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
                return cn.wthee.pcrtool.data.model.error()
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
        return cn.wthee.pcrtool.data.model.error()
    }

    /**
     * 获取排名信息
     */
    suspend fun getLeader(): ResponseData<LeaderData> {
        //请求
        try {
            val response = service.getLeader()
            if (response.message == "failure" || response.data == null) {
                return cn.wthee.pcrtool.data.model.error()
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
        return cn.wthee.pcrtool.data.model.error()
    }

    /**
     * 获取日历信息
     */
    suspend fun getCalendar(): ResponseData<CalendarData> {
        //请求
        try {
            val response = service.getCalendar()
            if (response.message == "failure" || response.data == null) {
                return cn.wthee.pcrtool.data.model.error()
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
        return cn.wthee.pcrtool.data.model.error()
    }

    /**
     * 获取通知信息
     */
    suspend fun getNotice(): ResponseData<List<AppNotice>> {
        //请求
        try {
            val response = service.getAppNotice()
            if (response.message == "failure" || response.data == null) {
                return cn.wthee.pcrtool.data.model.error()
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
        return cn.wthee.pcrtool.data.model.error()
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
                return cn.wthee.pcrtool.data.model.error()
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
        return cn.wthee.pcrtool.data.model.error()
    }
}
