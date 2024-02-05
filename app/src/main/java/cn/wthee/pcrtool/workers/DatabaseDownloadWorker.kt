package cn.wthee.pcrtool.workers

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.database.AppBasicDatabase
import cn.wthee.pcrtool.database.updateLocalDataBaseVersion
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.KEY_PROGRESS
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.NotificationUtil
import cn.wthee.pcrtool.utils.UnzippedUtil
import cn.wthee.pcrtool.utils.getString
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.coroutineScope
import java.io.File

/**
 * 数据库下载
 */
class DatabaseDownloadWorker(
    context: Context,
    parameters: WorkerParameters?,
) : CoroutineWorker(context, parameters!!) {

    private val downloadNotice = getString(R.string.title_download_database)
    //通知栏
    private val notificationManager: NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "1"
    private val noticeId = 0
    private lateinit var notification: NotificationCompat.Builder

    //适配低版本数据库路径
    private val folderPath = FileUtil.getDatabaseDir()

    companion object {
        const val KEY_FILE = "KEY_FILE"
        const val KEY_VERSION = "KEY_VERSION"
        const val KEY_REGION = "KEY_REGION"
    }

    override suspend fun doWork(): Result = coroutineScope {
        val inputData: Data = inputData
        //版本号
        val version = inputData.getString(KEY_VERSION) ?: return@coroutineScope Result.failure()
        val region = inputData.getInt(KEY_REGION, 2)
        val fileName = inputData.getString(KEY_FILE)
        setForegroundAsync(createForegroundInfo())
        return@coroutineScope download(version, region, fileName ?: "")
    }


    private suspend fun download(version: String, region: Int, fileName: String): Result {
        val responseBody: ByteArray?
        var progress = -2

        try {
            //创建下载请求
            val httpResponse: HttpResponse =
                DownloadFileClient.client.get(Constants.DATABASE_URL + fileName) {
                    onDownload { bytesSentTotal, contentLength ->
                        progress = (bytesSentTotal * 100.0 / contentLength).toInt()
                        if (contentLength < 1000) {
                            //文件大小异常
                            progress = -3
                        }
                        //更新下载进度
                        setProgressAsync(Data.Builder().putInt(KEY_PROGRESS, progress).build())
                        //取消通知
                        if (progress == -3 || progress == 100) {
                            notificationManager.cancelAll()
                        }
                    }
                }
            responseBody = httpResponse.body()!!
        } catch (e: Exception) {
            LogReportUtil.upload(e, Constants.EXCEPTION_DOWNLOAD_DB)
            return Result.failure(Data.Builder().putInt(KEY_PROGRESS, progress).build())
        }

        try {
            //创建数据库文件夹
            val file = File(folderPath)
            if (!file.exists()) {
                file.mkdir()
            }
            //br压缩包路径
            val dbZipPath = FileUtil.getDatabaseDir() + File.separator + fileName
            val db = File(dbZipPath)
            if (db.exists()) {
                //删除已有数据库文件
                FileUtil.deleteBr(RegionType.getByValue(region))
            }
            //保存
            db.writeBytes(responseBody)
            //关闭数据库
            AppBasicDatabase.close()
            //删除旧的wal
            FileUtil.apply {
                delete(getDatabaseWalPath(RegionType.CN))
                delete(getDatabaseWalPath(RegionType.TW))
                delete(getDatabaseWalPath(RegionType.JP))
            }
            //解压
            UnzippedUtil.deCompress(db, true)
            //更新数据库版本号
            updateLocalDataBaseVersion(version)
            return Result.success()
        } catch (e: Exception) {
            LogReportUtil.upload(e, Constants.EXCEPTION_SAVE_DB)
            return Result.failure(Data.Builder().putInt(KEY_PROGRESS, -2).build())
        }
    }

    //前台通知
    private fun createForegroundInfo(): ForegroundInfo {
        val context: Context = applicationContext

        notification = NotificationUtil.createNotice(
            context = context,
            channelId = channelId,
            channelName = getString(R.string.update_database),
            noticeTitle = downloadNotice,
            notificationManager = notificationManager
        )
        notification.setOngoing(true)
            .setProgress(100, 0, true)
        return ForegroundInfo(noticeId, notification.build())
    }

}