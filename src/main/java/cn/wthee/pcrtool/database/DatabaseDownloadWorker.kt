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
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.handler
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.network.model.DatabaseVersion
import cn.wthee.pcrtool.data.network.service.DatabaseService
import cn.wthee.pcrtool.utils.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


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
        const val KEY_VERSION = "KEY_VERSION"
        const val KEY_HASH = "KEY_HASH"
        const val KEY_VERSION_TYPE = "KEY_VERSION_TYPE"
        const val KEY_FROM_SETTING = "KEY_FROM_SETTING"
    }

    override suspend fun doWork(): Result = coroutineScope {
        val inputData: Data = inputData
        //版本号
        val version = inputData.getString(KEY_VERSION) ?: return@coroutineScope Result.failure()
        val hash = inputData.getString(KEY_HASH) ?: return@coroutineScope Result.failure()
        val type = inputData.getInt(KEY_VERSION_TYPE, 1)
        val fromSetting = inputData.getInt(KEY_FROM_SETTING, -1)
        setForegroundAsync(createForegroundInfo())
        //显示加载进度
        MainScope().launch {
            MainActivity.layoutDownload.visibility = View.VISIBLE
            MainActivity.textDownload.text = Constants.NOTICE_TITLE
            MainActivity.progressDownload.setProgress(1)
        }
        return@coroutineScope download(DatabaseVersion(version, hash), type, fromSetting)
    }


    private fun download(
        version: DatabaseVersion,
        type: Int,
        fromSetting: Int = -1
    ): Result {
        try {
            //创建Retrofit服务
            val service = ApiUtil.createWithClient(
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
                        notificationManager.notify(noticeId, notification.build())
                    }
                })
            )

            //下载文件
            val response =
                service.getDb(if (type == 1) Constants.DATABASE_DOWNLOAD_File_Name else Constants.DATABASE_DOWNLOAD_File_Name_JP)
                    .execute()

            //保存
            //创建数据库文件夹
            val file = File(folderPath)
            if (!file.exists()) {
                file.mkdir()
            }
            //删除已有数据库文件
            //br压缩包路径
            val dbZipPath = FileUtil.getDatabaseZipPath(type)
            val db = File(dbZipPath)
            if (db.exists()) {
                FileUtil.deleteDir(
                    folderPath,
                    arrayListOf(dbZipPath, FileUtil.getNewsDatabasePath())
                )
            }
            //写入文件
            FileUtil.save(response.body()!!.byteStream(), db)
            //更新数据库
            AppDatabase.getInstance().close()
            AppDatabaseJP.getInstance().close()
            UnzippedUtil.deCompress(db, true)
            notificationManager.cancelAll()
            //更新数据库版本号
            DatabaseUpdater.updateLocalDataBaseVersion(version)
            //通知更新数据
            if (fromSetting == 0 || fromSetting == 1) {
                //自动关闭应用
                handler.sendEmptyMessage(2)
            }
            if (fromSetting == -1) {
                //跳转
                MainScope().launch {
                    delay(1000L)
                    handler.sendEmptyMessage(1)
                }
            }
            MainScope().launch {
                MainActivity.textDownload.text = Constants.NOTICE_TOAST_SUCCESS
            }
            return Result.success()
        } catch (e: Exception) {
            MainScope().launch {
                ToastUtil.short(Constants.NOTICE_TOAST_NO_FILE)
            }
            notificationManager.cancelAll()
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