package cn.wthee.pcrtool.utils

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStream
import java.net.URL


class ImageDownloadUtil(
    private val context: Context
) {

    fun download(url: String) {
        //申请权限
        PermissionX.init(ActivityUtil.instance.currentActivity)
            .permissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .request { allGranted, grantedList, deniedList ->
                if(allGranted){
                    CoroutineScope(IO).launch {
                        try {
                            val picUrl = URL(url)
                            val bitmap = BitmapFactory.decodeStream(picUrl.openStream())
                            val name = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.')) + ".png"
                            Log.e("save", name)
                            saveBitmap(bitmap, name)
                        }catch (e: Exception){
                            Looper.prepare()
                            ToastUtil.short("图片已保存失败")
                            Looper.loop()
                        }
                    }
                }else{
                    ToastUtil.short("无法保存~请允许相关权限")
                }
            }
    }


    @Throws(IOException::class)
    private fun saveBitmap(bitmap: Bitmap, displayName: String
    ) {
        val relativeLocation = Environment.DIRECTORY_PICTURES
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/*")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        }
        val resolver = context.contentResolver
        var stream: OutputStream? = null
        var uri: Uri? = null
        try {
            val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            uri = resolver.insert(contentUri, contentValues)
            if (uri == null) {
                throw IOException("Failed to create new MediaStore record.")
            }
            stream = resolver.openOutputStream(uri)
            if (stream == null) {
                throw IOException("Failed to get output stream.")
            }
            if (!bitmap.compress(CompressFormat.PNG, 95, stream)) {
                throw IOException("Failed to save bitmap.")
            }
            Looper.prepare()
            ToastUtil.short("图片已保存到 $relativeLocation / $displayName")
            Looper.loop()
        } catch (e: IOException) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null)
            }
            throw e
        } finally {
            stream?.close()
        }
    }
}