package cn.wthee.pcrtool.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * 图片保存到本地
 */
class FileSaveHelper(private val context: Context) {

    /**
     * 保存bitmap
     */
    fun saveBitmap(bitmap: Bitmap, displayName: String): Boolean {
        var stream: OutputStream? = null
        val path = getImagePath()
        try {
            //保存属性
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES
                )
            }
            // 判断是否已存在
            val folder = File(path)
            if (!folder.exists()) {
                folder.mkdir()
            }
            val file = File("$path/$displayName")
            if (file.exists()) {
                ToastUtil.short("图片已存在")
                return true
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
            ToastUtil.short("保存成功\n$displayName")
            return true
        } catch (e: Exception) {
            ToastUtil.short("保存失败")
            return false
        } finally {
            stream?.close()
        }
    }

    /**
     * 保存文件
     */
    fun saveFile(toSaveFile: File, displayName: String): Boolean {
        try {
            //保存属性
            getDocPath()
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/*")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOCUMENTS + back_folder
                )
            }
            val resolver = context.contentResolver
            val contentUri: Uri = MediaStore.Files.getContentUri("external")
            val uri = query(displayName) ?: resolver.insert(contentUri, contentValues)
            val outputStream = resolver.openOutputStream(uri!!)
            outputStream?.use {
                it.write(toSaveFile.inputStream().readBytes())
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }


    /**
     * 导入文件
     */
    fun readFile(writeFile: File): Boolean {
        try {
            val uri = query(writeFile.name) ?: return false
            val resolver = context.contentResolver
            val inputStream = resolver.openInputStream(uri)!!
            FileUtil.save(inputStream, writeFile)
            inputStream.close()
            return true
        } catch (e: Exception) {
            return false
        }
    }


    /**
     * 文件查找
     */
    @SuppressLint("Recycle")
    private fun query(fileName: String): Uri? {
        lateinit var uri: Uri

        try {
            val contentUri: Uri = MediaStore.Files.getContentUri("external")
            val resolver = context.contentResolver
            val selection =
                "${MediaStore.MediaColumns.DISPLAY_NAME}='${fileName}' " +
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            "AND ${MediaStore.MediaColumns.RELATIVE_PATH}='${Environment.DIRECTORY_DOCUMENTS}$back_folder'"

                        } else {
                            ""
                        }
            val cursor = resolver.query(contentUri, null, selection, null, null)!!

            if (cursor.count == 0) {
                return null
            } else {
                while (cursor.moveToNext()) {
                    var displayNameIndex =
                        cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    if (displayNameIndex < 0) {
                        displayNameIndex = 0
                    }
                    var idNameIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
                    if (idNameIndex < 0) {
                        idNameIndex = 0
                    }
                    val findFileName = cursor.getString(displayNameIndex)
                    if (findFileName.equals(fileName)) {
                        val id = cursor.getLong(idNameIndex)
                        uri = ContentUris.withAppendedId(contentUri, id)
                        break
                    }
                }
            }
        } catch (e: Exception) {
        }
        return uri
    }

    companion object {
        const val back_folder = "/pcr_tool_backup/"

        /**
         * 获取图片保存路径
         */
        fun getImagePath(): String {
            var path: String = Environment.DIRECTORY_PICTURES
            path = "/storage/emulated/0" + File.separator + path
            return path
        }

        /**
         * 获取文档路径
         */
        fun getDocPath(): String {
            var path: String = Environment.DIRECTORY_DOCUMENTS
            path = "/storage/emulated/0/$path$back_folder"
            if (!File(path).exists()) {
                File(path).mkdir()
            }
            return path
        }
    }
}