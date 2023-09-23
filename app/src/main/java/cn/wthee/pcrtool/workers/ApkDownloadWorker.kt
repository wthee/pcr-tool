package cn.wthee.pcrtool.workers

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.network.FileService
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.utils.ApiUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.DOWNLOAD_APK_WORK
import cn.wthee.pcrtool.utils.DownloadListener
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.NotificationUtil
import cn.wthee.pcrtool.utils.getString
import kotlinx.coroutines.coroutineScope
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File


/**
 * 安装包下载
 */
class ApkDownloadWorker(
    context: Context,
    parameters: WorkerParameters?,
) : CoroutineWorker(context, parameters!!) {

    private val downloadNotice = getString(R.string.title_download_apk)

    //通知栏
    private val notificationManager: NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "2"
    private val noticeId = 0
    private lateinit var notification: NotificationCompat.Builder

    //适配低版本数据库路径
    private val folderPath = FileUtil.getApkDir()
    private lateinit var service: Call<ResponseBody>

    companion object {
        const val KEY_URL = "KEY_URL"
    }

    override suspend fun doWork(): Result = coroutineScope {
        val inputData: Data = inputData
        val downloadUrl = inputData.getString(KEY_URL) ?: return@coroutineScope Result.failure()
        setForegroundAsync(createForegroundInfo())
        val result = download(downloadUrl)
        if (result == Result.failure()) {
            WorkManager.getInstance(MyApplication.context).cancelUniqueWork(DOWNLOAD_APK_WORK)
        } else if (result == Result.success()) {
            navViewModel.apkDownloadProgress.postValue(-2)
        }
        return@coroutineScope result
    }


    private fun download(downloadUrl: String): Result {
        val response: Response<ResponseBody>?
        val apkName = downloadUrl.split("/").last()
        val apk: File
        val baseUrl = downloadUrl.substring(0, downloadUrl.lastIndexOf("/") + 1)

        try {
            //创建Retrofit服务
            service = ApiUtil.createWithClient(
                FileService::class.java,
                baseUrl,
                ApiUtil.buildDownloadClient(object : DownloadListener {
                    //下载进度
                    override fun onProgress(progress: Int, currSize: Long, totalSize: Long) {
                        try {
                            //更新下载进度
                            navViewModel.apkDownloadProgress.postValue(progress)
                        } catch (_: Exception) {

                        }
                    }

                    override fun onFinish() {
                        //下载完成
                        navViewModel.apkDownloadProgress.postValue(100)
                        notificationManager.cancelAll()
                    }
                })
            ).getFile(apkName)
            //下载文件
            response = service.execute()
            //创建apk文件夹
            val apkDir = File(folderPath)
            if (!apkDir.exists()) {
                apkDir.mkdir()
            }
            //br压缩包路径
            val apkPath = folderPath + File.separator + apkName
            apk = File(apkPath)
            if (apk.exists()) {
                //删除已有文件
                FileUtil.delete(apkDir, saveDir = true)
            }
            //保存
            FileUtil.save(response!!.body()!!.byteStream(), apk)

        } catch (e: Exception) {
            navViewModel.apkDownloadProgress.postValue(-3)
            LogReportUtil.upload(e, Constants.EXCEPTION_DOWNLOAD_APK)
            return Result.failure()
        }

        return try {
            openAPK(apk)
            Result.success()
        } catch (e: Exception) {
            navViewModel.apkDownloadProgress.postValue(-4)
            LogReportUtil.upload(e, Constants.EXCEPTION_DOWNLOAD_APK)
            Result.failure()
        }
    }

    //前台通知
    private fun createForegroundInfo(): ForegroundInfo {
        val context: Context = applicationContext

        notification = NotificationUtil.createNotice(
            context = context,
            channelId = channelId,
            channelName = getString(R.string.update_apk),
            noticeTitle = downloadNotice,
            notificationManager = notificationManager
        )
        notification.setOngoing(true)
            .setProgress(100, 0, true)
        return ForegroundInfo(noticeId, notification.build())
    }


    /**
     * 安装apk
     */
    private fun openAPK(apkFile: File) {
        val auth = BuildConfig.APPLICATION_ID + ".provider"
        val type = "application/vnd.android.package-archive"

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(
            FileProvider.getUriForFile(
                applicationContext,
                auth,
                apkFile
            ),
            type
        )
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        applicationContext.startActivity(intent)
    }
}