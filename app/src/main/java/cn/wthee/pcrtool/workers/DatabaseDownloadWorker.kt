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
import cn.wthee.pcrtool.data.network.downloadFileClient
import cn.wthee.pcrtool.database.AppBasicDatabase
import cn.wthee.pcrtool.ui.home.DbDownloadState
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.KEY_PROGRESS
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.NotificationUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.UnzippedUtil
import cn.wthee.pcrtool.utils.getString
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import java.io.File

/**
 * 数据库下载
 */
class DatabaseDownloadWorker(
    context: Context,
    parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {

    private val downloadNotice = getString(R.string.title_db_downloading)

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
        const val KEY_REGION = "KEY_REGION"
    }

    override suspend fun doWork(): Result = coroutineScope {
        val inputData: Data = inputData
        //版本号
        val region = inputData.getInt(KEY_REGION, RegionType.CN.value)
        val fileName = inputData.getString(KEY_FILE)
        setForegroundAsync(createForegroundInfo())
        val result = download(region, fileName ?: "")
        return@coroutineScope result
    }

    /**
     * 下载数据并保存
     * @param region 数据区域版本
     * @param fileName 需下载的文件名
     */
    private suspend fun download(region: Int, fileName: String): Result {
        val responseBody: ByteArray?
        var progress: Int

        //数据开始下载提示
        ToastUtil.launchShort(getString(R.string.title_db_downloading))

        try {
            //创建下载请求
            val httpResponse: HttpResponse =
                downloadFileClient.get(Constants.DATABASE_URL + fileName) {
                    onDownload { bytesSentTotal, contentLength ->
                        progress = (bytesSentTotal * 100.0 / contentLength).toInt()
                        if (contentLength < 1000) {
                            //文件大小异常
                            progress = DbDownloadState.SIZE_ERROR.state
                        }
                        //更新下载进度
                        setProgressAsync(Data.Builder().putInt(KEY_PROGRESS, progress).build())
                        //取消通知
                        if (progress == DbDownloadState.SIZE_ERROR.state || progress == 100) {
                            notificationManager.cancelAll()
                        }
                    }
                }
            responseBody = httpResponse.body()!!
        } catch (e: Exception) {
            if (e is CancellationException) {
                ToastUtil.launchShort(getString(R.string.db_download_cancel))
            } else {
                ToastUtil.launchShort(getString(R.string.db_download_failure))
                LogReportUtil.upload(e, Constants.EXCEPTION_DOWNLOAD_DB)
            }
            return Result.failure()
        }

        try {
            //创建数据库文件夹
            val file = File(folderPath)
            if (!file.exists()) {
                file.mkdir()
            }
            //br压缩包路径
            val dbBrPath = FileUtil.getDatabaseDir() + File.separator + fileName
            val dbBr = File(dbBrPath)
            if (dbBr.exists()) {
                //删除已有数据库br压缩文件
                FileUtil.delete(
                    FileUtil.getDatabasePath(
                        RegionType.getByValue(region),
                        Constants.DATABASE_BR
                    )
                )
            }
            //保存br文件
            dbBr.writeBytes(responseBody)
            //关闭数据库
            AppBasicDatabase.close()
            //解压
            UnzippedUtil.deCompress(dbBr, true)
            return Result.success()
        } catch (e: Exception) {
            LogReportUtil.upload(e, Constants.EXCEPTION_SAVE_DB)
            ToastUtil.launchShort(getString(R.string.db_load_failure))
            return Result.failure()
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