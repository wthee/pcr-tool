package cn.wthee.pcrtool.utils

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * 图片保存到本地
 */
class ImageDownloadUtil(
    private val activity: FragmentActivity
) {

    val context: Context = activity.applicationContext

    fun save(bitmap: Bitmap, name: String) {
        //申请权限
        PermissionX.init(activity)
            .permissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .request { allGranted, _, _ ->
                if (allGranted) {
                    activity.runOnUiThread {
                        ToastUtil.short("正在保存，请稍后~")
                        saveBitmap(bitmap, name)
                    }
                } else {
                    ToastUtil.short("无法保存~请允许相关权限")
                }
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
                var uri: Uri? = null
                val resolver = context.contentResolver
                try {
                    val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    uri = resolver.insert(contentUri, contentValues)
                    stream = resolver.openOutputStream(uri!!)
                    bitmap.compress(CompressFormat.PNG, 100, stream)
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, false)
                    resolver.update(uri, contentValues, null, null)
                } catch (e: IOException) {
                    if (uri != null) {
                        resolver.delete(uri, null, null)
                    }
                }
            } else {
                path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator
                val directory = File(path)
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                val file = File(directory, displayName)
                stream = FileOutputStream(file)
                contentValues.put(MediaStore.Images.Media.DATA, file.absolutePath)
                bitmap.compress(CompressFormat.PNG, 100, stream)
                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
            }
            ToastUtil.short("图片保存成功~\n$path/$displayName")
        } catch (e: Exception) {
            Log.e("save", e.message ?: "")
            ToastUtil.short("图片保存失败")
        } finally {
            stream?.close()
        }
    }

}