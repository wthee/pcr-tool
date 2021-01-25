package cn.wthee.pcrtool.utils

import android.os.Build
import cn.wthee.pcrtool.MyApplication
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 文件路径获取
 */
object FileUtil {

    /**
     * 数据库所在文件夹
     */
    fun getDatabaseDir() = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        MyApplication.context.dataDir.absolutePath
    else {
        val path = MyApplication.context.filesDir.absolutePath
        path.substring(0, path.length - 6)
    } + "/databases"

    /**
     * 数据库路径
     */
    fun getDatabasePath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_Name else Constants.DATABASE_Name_JP

    /**
     * wal 文件路径
     */
    private fun getDatabaseWalPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_WAL else Constants.DATABASE_WAL_JP

    /**
     * shm 文件路径
     */
    private fun getDatabaseShmPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_SHM else Constants.DATABASE_SHM_JP

    /**
     * 数据库压缩文件路径
     */
    fun getDatabaseZipPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_DOWNLOAD_File_Name else Constants.DATABASE_DOWNLOAD_File_Name_JP

    /**
     * 数据库是否需要判断
     */
    fun needUpdate(type: Int) =
        !File(getDatabasePath(type)).exists()
                || File(getDatabasePath(type)).length() < 1 * 1024 * 1024
                || File(getDatabaseWalPath(type)).length() < 1 * 1024

    /**
     * 删除数据库文件
     */
    fun deleteMainDatabase(type: Int) {
        val db = File(getDatabasePath(type))
        if (db.exists()) {
            db.delete()
        }
        val wal = File(getDatabaseWalPath(type))
        if (wal.exists()) {
            wal.delete()
        }
        val shm = File(getDatabaseShmPath(type))
        if (shm.exists()) {
            shm.delete()
        }
    }

    /**
     * 保存文件
     */
    fun save(input: InputStream, output: File) {
        val out = FileOutputStream(output)
        val byte = ByteArray(1024 * 4)
        var line: Int
        while (input.read(byte).also { line = it } > 0) {
            out.write(byte, 0, line)
        }
        out.flush()
        out.close()
        input.close()
    }

}