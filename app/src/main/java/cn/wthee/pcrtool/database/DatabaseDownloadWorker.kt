package cn.wthee.pcrtool.database

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import androidx.work.*
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.data.network.DatabaseService
import cn.wthee.pcrtool.ui.MainActivity.Companion.handler
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.utils.*
import kotlinx.coroutines.coroutineScope
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
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
    private lateinit var service: Call<ResponseBody>

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
        val result = download(version, region, fileName ?: "")
        if (result == Result.success()) {
            //通知更新数据
            handler.sendEmptyMessage(region)
        } else if (result == Result.failure()) {
            WorkManager.getInstance(MyApplication.context).cancelAllWork()
        }
        return@coroutineScope result
    }


    private fun download(version: String, region: Int, fileName: String): Result {
        var response: Response<ResponseBody>? = null
        try {
            //创建Retrofit服务
            service = ApiUtil.createWithClient(
                DatabaseService::class.java, Constants.DATABASE_URL,
                ApiUtil.buildDownloadClient(object : DownloadListener {
                    //下载进度
                    override fun onProgress(progress: Int, currSize: Long, totalSize: Long) {
                        try {
                            //更新下载进度
                            navViewModel.downloadProgress.postValue(progress)
                        } catch (_: Exception) {

                        }
                        //更新下载进度
//                        notification.setProgress(100, progress, false)
//                            .setContentTitle(
//                                "$downloadNotice ${currSize / 1024}  / ${totalSize / 1024}"
//                            )
//                        notificationManager.notify(noticeId, notification.build())
                    }

                    override fun onFinish() {
                        //下载完成
                        navViewModel.downloadProgress.postValue(100)
                        notificationManager.cancelAll()
                    }
                })
            ).getDb(fileName)
            //下载文件
            response = service.execute()
        } catch (e: Exception) {
            LogReportUtil.upload(e, Constants.EXCEPTION_DOWNLOAD_DB)
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
                FileUtil.deleteBr(region)
            }
            //保存
            FileUtil.save(response!!.body()!!.byteStream(), db)
            //关闭数据库
            AppBasicDatabase.close()
            //删除旧的wal
            FileUtil.apply {
                delete(getDatabaseBackupWalPath(RegionType.CN))
                delete(getDatabaseBackupWalPath(RegionType.TW))
                delete(getDatabaseBackupWalPath(RegionType.JP))
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