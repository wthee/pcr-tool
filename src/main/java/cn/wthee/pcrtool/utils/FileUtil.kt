package cn.wthee.pcrtool.utils

import android.os.Build
import android.util.Log
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import java.io.File

object FileUtil {

    //数据库所在文件夹
    fun getDatabaseDir() = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        MyApplication.getContext().dataDir.absolutePath
    else {
        val path = MyApplication.getContext().filesDir.absolutePath
        path.substring(0, path.length - 6)
    } + "/databases"

    //数据库路径
    public fun getDatabasePath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_Name else Constants.DATABASE_Name_JP

    private fun getDatabaseWalPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_WAL else Constants.DATABASE_WAL_JP

    fun getDatabaseZipPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_DOWNLOAD_File_Name else Constants.DATABASE_DOWNLOAD_File_Name_JP

    //数据库判断
    fun needUpadateDb(type: Int) =
        !File(getDatabasePath(type)).exists()
                || File(getDatabasePath(type)).length() < 1 * 1024 * 1024
                || File(getDatabaseWalPath(type)).length() < 1 * 1024

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