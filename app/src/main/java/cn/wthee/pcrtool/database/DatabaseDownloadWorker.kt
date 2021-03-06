package cn.wthee.pcrtool.database

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.*
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.handler
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.network.service.DatabaseService
import cn.wthee.pcrtool.utils.*
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

/**
 * 数据库下载
 */
class DatabaseDownloadWorker(
    @NonNull context: Context,
    @NonNull parameters: WorkerParameters?
) : CoroutineWorker(context, parameters!!) {

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
        const val KEY_VERSION_TYPE = "KEY_VERSION_TYPE"
        const val KEY_FROM = "KEY_FROM"

        lateinit var service: Call<ResponseBody>
    }

    override suspend fun doWork(): Result = coroutineScope {
        val inputData: Data = inputData
        //版本号
        val version = inputData.getString(KEY_VERSION) ?: return@coroutineScope Result.failure()
        val type = inputData.getInt(KEY_VERSION_TYPE, 1)
        val fileName = inputData.getString(KEY_FILE)
        val from = inputData.getInt(KEY_FROM, -1)
        setForegroundAsync(createForegroundInfo())
        //显示加载进度
        MainScope().launch {
            MainActivity.layoutDownload.visibility = View.VISIBLE
            MainActivity.textDownload.text = Constants.NOTICE_TITLE
        }
        val result = download(version, type, fileName ?: "")
        if (result == Result.success()) {
            //通知更新数据
            handler.sendEmptyMessage(from)
        }
        return@coroutineScope result
    }


    private fun download(version: String, type: Int, fileName: String): Result {
        var response: Response<ResponseBody>? = null
        try {
            //创建Retrofit服务
            service = ApiUtil.createWithClient(
                DatabaseService::class.java, Constants.DATABASE_URL,
                ApiUtil.downloadClientBuild(object : DownloadListener {
                    //下载进度
                    override fun onProgress(progress: Int, currSize: Long, totalSize: Long) {
                        //更新下载进度
                        notification.setProgress(100, progress, false)
                            .setContentTitle(
                                "${Constants.NOTICE_TITLE} $currSize  / $totalSize"
                            )
                        MainActivity.progressDownload.setProgress(progress)
                        notificationManager.notify(noticeId, notification.build())
                    }

                    override fun onFinish() {
                        //下载完成
                        notification.setProgress(100, 100, false)
                            .setContentTitle("${Constants.NOTICE_TOAST_SUCCESS} ")
                        notificationManager.cancelAll()
                    }
                })
            ).getDb(fileName)
            //下载文件
            response = service.execute()
        } catch (e: Exception) {
            MainScope().launch {
                UMCrash.generateCustomLog(e, Constants.EXCEPTION_DOWNLOAD_DB)
            }
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
                FileUtil.deleteMainDatabase(type)
            }
            //保存
            FileUtil.save(response!!.body()!!.byteStream(), db)
            //删除旧的wal
            FileUtil.apply {
                delete(getDatabaseBackupWalPath(1))
                delete(getDatabaseBackupWalPath(2))
                delete(getDatabaseWalPath(1))
                delete(getDatabaseWalPath(2))
            }
            //关闭数据库
            AppDatabase.close()
            AppDatabaseJP.close()
            //加压缩
            UnzippedUtil.deCompress(db, true)
            //更新数据库版本号
            DatabaseUpdater.updateLocalDataBaseVersion(version)
            return Result.success()
        } catch (e: Exception) {
            MainScope().launch {
                UMCrash.generateCustomLog(e, Constants.EXCEPTION_SAVE_DB)
            }
            return Result.failure()
        }
    }

    //前台通知
    private fun createForegroundInfo(): ForegroundInfo {
        val context: Context = applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(Constants.NOTICE_TITLE)
            .setTicker(Constants.NOTICE_TITLE)
            .setSmallIcon(R.mipmap.ic_logo)
            .setOngoing(true)
            .setProgress(100, 0, true)
        return ForegroundInfo(noticeId, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(channelId, "数据更新", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }


}