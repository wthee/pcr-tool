package cn.wthee.pcrtool.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * 图片保存到本地
 */
class ImageDownloadHelper(
    private val activity: FragmentActivity
) {

    val context: Context = activity.applicationContext

    fun save(bitmap: Bitmap, name: String) {
        activity.lifecycleScope.launch {
            saveBitmap(bitmap, name)
        }
    }

    //保存bitmap
    private fun saveBitmap(
        bitmap: Bitmap, displayName: String
    ) {
        var stream: OutputStream? = null
        var path: String
        try {
            //保存属性
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
            path = Environment.DIRECTORY_PICTURES
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, path)
            }
            // 判断是否已存在
            path = "/storage/emulated/0" + File.separator + path
            val file = File("$path/$displayName")
            if (file.exists()) {
                ToastUtil.short("图片已存在~")
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                var uri: Uri? = null
                val resolver = context.contentResolver
                try {
                    val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    uri = resolver.insert(contentUri, contentValues)
                    stream = resolver.openOutputStream(uri!!)
                    bitmap.compress(CompressFormat.PNG, 100, stream)
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, false)
                    resolver.update(uri, contentValues, null, null)
                } catch (e: Exception) {
                    if (uri != null) {
                        resolver.delete(uri, null, null)
                    }
                }
            } else {
                stream = FileOutputStream(file)
                contentValues.put(MediaStore.Images.Media.DATE_ADDED, file.absolutePath)
                bitmap.compress(CompressFormat.PNG, 100, stream)
                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
            }
            ToastUtil.short("图片保存成功~$displayName")
        } catch (e: Exception) {
            ToastUtil.short("图片保存失败")
        } finally {
            stream?.close()
        }
    }

}