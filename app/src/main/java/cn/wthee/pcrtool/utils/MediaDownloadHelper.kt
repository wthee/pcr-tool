package cn.wthee.pcrtool.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * 媒体文件保存
 */
@OptIn(UnstableApi::class)
class MediaDownloadHelper(private val context: Context) {


    companion object {
        const val DIR = "/storage/emulated/0"

        //文件夹名
        private val baseDir = "pcr"

        /**
         * 获取保存路径
         */
        fun getSaveDir(isVideo: Boolean): String {
            val environment = if (isVideo) {
                Environment.DIRECTORY_MOVIES
            } else {
                Environment.DIRECTORY_PICTURES
            }
            return DIR + File.separator + environment + File.separator + baseDir
        }
    }


    /**
     * 下载视频
     *
     * @param url 下载地址
     * @param fileName 保存后的文件名
     * @param lifecycleOwner
     * @param onFinished 保存结束（成功或失败）监听
     * @param onDownloadFailure 下载失败监听
     */
    fun downloadVideo(
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
                            saveMedia(videoPath = sourceFile, displayName = fileName)
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
     * 保存媒体文件
     *
     * @param videoPath 视频文件路径
     * @param bitmap 图片
     */
    fun saveMedia(
        videoPath: String? = null,
        bitmap: Bitmap? = null,
        displayName: String
    ) {
        MainScope().launch(Dispatchers.IO) {
            val isVideo = bitmap == null

            val path = getSaveDir(isVideo)
            try {
                //保存属性
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                if (isVideo) {
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                } else {
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
                }

                // 判断是否已存在
                val folder = File(path)
                if (!folder.exists()) {
                    folder.mkdir()
                }
                val file = File("$path/$displayName")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val environment = if (isVideo) {
                        Environment.DIRECTORY_MOVIES
                    } else {
                        Environment.DIRECTORY_PICTURES
                    }
                    contentValues.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        environment + File.separator + baseDir
                    )
                } else {
                    if (isVideo) {
                        contentValues.put(MediaStore.Images.Media.DATA, file.absolutePath)
                    } else {
                        contentValues.put(MediaStore.Video.Media.DATA, file.absolutePath)
                    }
                }

                //保存
                val videoFile = if (videoPath == null) {
                    null
                } else {
                    File(videoPath)
                }
                if (insertMedia(videoFile, bitmap, contentValues, isVideo)) {
                    VibrateUtil(context).done()
                    ToastUtil.launchShort(
                        getString(
                            R.string.save_success,
                            file.absolutePath.replace(DIR, "")
                        )
                    )
                } else {
                    VibrateUtil(context).error()
                    ToastUtil.launchShort(getString(R.string.save_failure))
                }
            } catch (e: Exception) {
                VibrateUtil(context).error()
                LogReportUtil.upload(e, Constants.EXCEPTION_FILE_SAVE + videoPath + displayName)
                ToastUtil.launchShort(getString(R.string.save_error))
            }
        }
    }

    /**
     * 保存媒体文件
     *
     * @param videoFile 视频文件
     * @param bitmap 图片
     */
    private fun insertMedia(
        videoFile: File?,
        bitmap: Bitmap?,
        contentValues: ContentValues,
        isVideo: Boolean
    ): Boolean {
        var uri: Uri? = null
        val resolver = context.contentResolver

        try {
            uri = if (isVideo) {
                resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            }
            //保存
            resolver.openOutputStream(uri!!)?.use {
                if (isVideo) {
                    if (videoFile != null) {
                        Files.copy(videoFile, it)
                    } else {

                    }
                } else {
                    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            }
            val row = resolver.update(uri, contentValues, null, null)
            if (row > 0) {
                videoFile?.delete()
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