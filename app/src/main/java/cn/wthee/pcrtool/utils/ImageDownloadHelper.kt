package cn.wthee.pcrtool.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import cn.wthee.pcrtool.R
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * 图片保存到本地
 * fixme 合并图片视频保存代码
 */
class ImageDownloadHelper(private val context: Context) {

    companion object {
        const val DIR = "/storage/emulated/0"

        //文件夹名
        private const val baseDir = "pcr"

        /**
         * 获取保存路径
         */
        fun getSaveDir(): String {
            var path: String = Environment.DIRECTORY_PICTURES
            path = DIR + File.separator + path + File.separator + baseDir
            return path
        }
    }

    /**
     * 保存bitmap
     */
    fun saveBitmap(bitmap: Bitmap, displayName: String) {
        MainScope().launch(IO) {
            val path = getSaveDir()
            try {
                //保存属性
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*")

                // 判断是否已存在
                val folder = File(path)
                if (!folder.exists()) {
                    folder.mkdir()
                }
                val file = File("$path/$displayName")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + File.separator + baseDir
                    )
                } else {
                    contentValues.put(MediaStore.Images.Media.DATA, file.absolutePath)
                }

                //保存
                if (insertImage(bitmap, contentValues)) {
                    VibrateUtil(context).done()
                    Looper.prepare()
                    ToastUtil.short(
                        getString(
                            R.string.save_success,
                            file.absolutePath.replace(DIR, "")
                        )
                    )
                    Looper.loop()
                } else {
                    VibrateUtil(context).error()
                    Looper.prepare()
                    ToastUtil.short(getString(R.string.save_failure))
                    Looper.loop()
                }

            } catch (e: Exception) {
                VibrateUtil(context).error()
                Looper.prepare()
                ToastUtil.short(getString(R.string.save_error))
                Looper.loop()
            }
        }
    }

    /**
     * 保存图片
     */
    private fun insertImage(
        bitmap: Bitmap,
        contentValues: ContentValues
    ): Boolean {
        var uri: Uri? = null
        val resolver = context.contentResolver
        var saveSuccess = false

        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            resolver.openOutputStream(uri!!)?.use {
                //保存
                saveSuccess = bitmap.compress(CompressFormat.PNG, 100, it)
            }

            resolver.update(uri, contentValues, null, null)
        } catch (e: Exception) {
            if (uri != null) {
                resolver.delete(uri, null, null)
            }
        }
        return saveSuccess
    }

}