package cn.wthee.pcrtool.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.workers.FileDownloadWorker
import com.google.common.io.Files
import java.io.File

/**
 * 视频下载
 * fixme 合并图片视频保存代码
 */
@OptIn(UnstableApi::class)
class VideoDownloadHelper(private val context: Context) {


    companion object {
        const val DIR = "/storage/emulated/0"

        //文件夹名
        private const val baseDir = "pcr"

        /**
         * 获取保存路径
         */
        fun getSaveDir(): String {
            var path: String = Environment.DIRECTORY_MOVIES
            path = DIR + File.separator + path + File.separator + baseDir
            return path
        }
    }

    /**
     * 下载视频
     *
     * @param url 下载地址
     * @param lifecycleOwner
     * @param onFinished 保存结束（成功或失败）监听
     * @param onDownloadFailure 下载失败监听
     */
    fun download(
        url: String,
        fileName: String,
        lifecycleOwner: LifecycleOwner,
        onFinished: () -> Unit,
        onDownloadFailure: () -> Unit
    ) {

        //创建 work
        val data = Data.Builder()
            .putString(FileDownloadWorker.KEY_URL, url)
            .putString(FileDownloadWorker.KEY_FILE_NAME, fileName)
            .build()
        val request =
            OneTimeWorkRequestBuilder<FileDownloadWorker>()
                .setInputData(data)
                .build()

        val workManager = WorkManager.getInstance(MyApplication.context)
        workManager.enqueueUniqueWork(
            Constants.DOWNLOAD_FILE_WORK,
            ExistingWorkPolicy.APPEND,
            request
        )

        //监听下载进度
        workManager.getWorkInfoByIdLiveData(request.id)
            .observe(lifecycleOwner) { workInfo: WorkInfo? ->
                if (workInfo != null) {
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            //下载成功，保存
                            val sourceFile = FileUtil.getFileDir() + File.separator + fileName
                            saveVideo(sourceFile, fileName)
                            onFinished()
                        }

                        WorkInfo.State.FAILED -> {
                            onDownloadFailure()
                        }

                        else -> Unit
                    }
                }
            }
    }

    /**
     * 保存视频
     */
    private fun saveVideo(videoPath: String, displayName: String): Boolean {
        val path = getSaveDir()
        val tempFile = File(videoPath)
        try {
            //保存属性
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")

            // 判断是否已存在
            val folder = File(path)
            if (!folder.exists()) {
                folder.mkdir()
            }
            val file = File("$path/$displayName")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_MOVIES + File.separator + baseDir
                )
            } else {
                contentValues.put(MediaStore.Video.Media.DATA, file.absolutePath)
            }

            //保存
            if (insertVideo(tempFile, contentValues)) {
                VibrateUtil(context).done()
                ToastUtil.launchShort(
                    getString(
                        R.string.save_success,
                        file.absolutePath.replace(DIR, "")
                    )
                )
                return true
            } else {
                VibrateUtil(context).error()
                ToastUtil.launchShort(getString(R.string.save_failure))
            }
        } catch (e: Exception) {
            VibrateUtil(context).error()
            LogReportUtil.upload(e, Constants.EXCEPTION_FILE_SAVE + videoPath + displayName)
            ToastUtil.launchShort(getString(R.string.save_error))
        }

        return false
    }

    /**
     * 保存图片
     */
    private fun insertVideo(
        file: File,
        contentValues: ContentValues
    ): Boolean {
        var uri: Uri? = null
        val resolver = context.contentResolver

        try {
            uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            resolver.openOutputStream(uri!!)?.use {
                //保存
                Files.copy(file, it)
            }
            val row = resolver.update(uri, contentValues, null, null)
            if (row > 0) {
                file.delete()
            }
            return row > 0
        } catch (e: Exception) {
            if (uri != null) {
                resolver.delete(uri, null, null)
            }
        }
        return false
    }
}