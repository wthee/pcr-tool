package cn.wthee.pcrtool.workers

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.DOWNLOAD_FILE_WORK
import cn.wthee.pcrtool.utils.Constants.KEY_PROGRESS
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.NotificationUtil
import cn.wthee.pcrtool.utils.getString
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.coroutineScope
import java.io.File


/**
 * 文件下载
 */
class FileDownloadWorker(
    context: Context,
    parameters: WorkerParameters?,
) : CoroutineWorker(context, parameters!!) {

    private val downloadNotice = getString(R.string.title_download_file)

    //通知栏
    private val notificationManager: NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "download_file"
    private val noticeId = 0
    private lateinit var notification: NotificationCompat.Builder

    private val folderPath = FileUtil.getDownloadDir()

    companion object {
        const val KEY_URL = "KEY_URL"
        const val KEY_FILE_NAME = "KEY_FILE_NAME"
    }

    override suspend fun doWork(): Result = coroutineScope {
        val inputData: Data = inputData
        val downloadUrl = inputData.getString(KEY_URL) ?: return@coroutineScope Result.failure()
        val rename = inputData.getString(KEY_FILE_NAME)
        setForegroundAsync(createForegroundInfo())
        val result = download(downloadUrl, rename)
        if (result == Result.failure()) {
            WorkManager.getInstance(MyApplication.context).cancelUniqueWork(DOWNLOAD_FILE_WORK)
        } else if (result == Result.success()) {
            setProgressAsync(Data.Builder().putInt(KEY_PROGRESS, -2).build())
        }
        return@coroutineScope result
    }


    /**
     * 下载文件并保存
     * @param downloadUrl 文件url
     * @param rename 重命名的文件名
     */
    private suspend fun download(downloadUrl: String, rename: String?): Result {
        val fileName = downloadUrl.split("/").last()
        val file: File

        try {
            //创建文件夹
            val folder = File(folderPath)
            if (!folder.exists()) {
                folder.mkdir()
            }
            //创建下载请求
            val httpResponse: HttpResponse = DownloadFileClient.client.get(downloadUrl) {
                onDownload { bytesSentTotal, contentLength ->
                    val progress = (bytesSentTotal * 100.0 / contentLength).toInt()
                    //更新下载进度
                    setProgressAsync(Data.Builder().putInt(KEY_PROGRESS, progress).build())
                    if (progress == 100) {
                        notificationManager.cancelAll()
                    }
                }
            }
            val responseBody: ByteArray = httpResponse.body()
            //文件路径，判断是否需要重命名
            val existFilePath = folderPath + File.separator + (rename ?: fileName)
            file = File(existFilePath)
            if (file.exists()) {
                //删除已有文件
                FileUtil.delete(file, saveDir = true)
            }
            //保存
            file.writeBytes(responseBody)
        } catch (e: Exception) {
            setProgressAsync(Data.Builder().putInt(KEY_PROGRESS, -3).build())
            LogReportUtil.upload(e, Constants.EXCEPTION_DOWNLOAD_FILE)
            return Result.failure()
        }

        return Result.success()
    }

    //前台通知
    private fun createForegroundInfo(): ForegroundInfo {
        val context: Context = applicationContext

        notification = NotificationUtil.createNotice(
            context = context,
            channelId = channelId,
            channelName = getString(R.string.download_file_notice),
            noticeTitle = downloadNotice,
            notificationManager = notificationManager
        )
        notification.setOngoing(true)
            .setProgress(100, 0, true)
        return ForegroundInfo(noticeId, notification.build())
    }

}