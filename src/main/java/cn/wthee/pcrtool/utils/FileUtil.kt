package cn.wthee.pcrtool.utils

import android.util.Log
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import java.io.File

object FileUtil {

    //迭代删除文件夹里的内容(不包括文件夹)
    fun deleteDir(dirPath: String, notDel: String) {
        val file = File(dirPath)
        if (file.isFile && file.path != notDel) {
            Log.e(LOG_TAG, file.path)
            file.delete()
        } else {
            val files = file.listFiles()
            if (files != null) {
                for (i in files.indices) {
                    deleteDir(files[i].absolutePath, notDel)
                }
            }
        }
    }

}