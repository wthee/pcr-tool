package cn.wthee.pcrtool.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSourceInputStream
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import cn.wthee.pcrtool.R
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
        private const val SAVE_DIR = "pcr"

        /**
         * 获取保存路径
         */
        fun getSaveDir(isVideo: Boolean): String {
            val environment = if (isVideo) {
                Environment.DIRECTORY_MOVIES
            } else {
                Environment.DIRECTORY_PICTURES
            }
            return DIR + File.separator + environment + File.separator + SAVE_DIR
        }
    }


    /**
     * 使用缓存保存视频文件
     */
    fun saveCachedVideo(
        context: Context,
        videoUrl: String,
        fileName: String,
        cache: Cache
    ) {
        MainScope().launch(Dispatchers.IO) {
            try {
                val dataSource = CacheDataSource.Factory()
                    .setCache(cache)
                    .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
                    .createDataSource()

                val dataSpec = DataSpec(Uri.parse(videoUrl))
                val inputStream = DataSourceInputStream(dataSource, dataSpec)
                //创建文件夹
                val folder = File(FileUtil.getDownloadDir())
                if (!folder.exists()) {
                    folder.mkdir()
                }
                //把缓存写入文件
                val sourceFile = File(FileUtil.getDownloadDir() + File.separator + fileName)
                if (!sourceFile.exists()) {
                    sourceFile.createNewFile()
                }
                inputStream.use { input ->
                    sourceFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                //保存文件
                saveMedia(videoFile = sourceFile, displayName = fileName) {}
            } catch (e: Exception) {
                LogReportUtil.upload(e, Constants.EXCEPTION_FILE_SAVE + fileName)
                VibrateUtil(context).error()
                ToastUtil.launchShort(getString(R.string.save_failure))
            }
        }
    }

    /**
     * 保存媒体文件
     *
     * @param videoFile 视频文件
     * @param bitmap 图片
     */
    fun saveMedia(
        videoFile: File? = null,
        bitmap: Bitmap? = null,
        displayName: String,
        onSaved: () -> Unit
    ) {
        MainScope().launch(Dispatchers.IO) {
            var saveSuccess = false
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
                        environment + File.separator + SAVE_DIR
                    )
                } else {
                    if (isVideo) {
                        contentValues.put(MediaStore.Images.Media.DATA, file.absolutePath)
                    } else {
                        contentValues.put(MediaStore.Video.Media.DATA, file.absolutePath)
                    }
                }

                //保存
                if (insertMedia(videoFile, bitmap, contentValues, isVideo)) {
                    VibrateUtil(context).done()
                    saveSuccess = true
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
                LogReportUtil.upload(e, Constants.EXCEPTION_FILE_SAVE + displayName)
                ToastUtil.launchShort(getString(R.string.save_error))
            }
            //删除下载缓存
            videoFile?.delete()
            if (saveSuccess) {
                onSaved()
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
        } catch (_: Exception) {
            if (uri != null) {
                resolver.delete(uri, null, null)
            }
        }
        return false
    }
}