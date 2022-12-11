package cn.wthee.pcrtool.utils

import android.content.Context
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
    fun getDatabaseDir(context: Context = MyApplication.context) = getAppDir(context) + "/databases"

    /**
     * 获取 App 内部存储路径
     */
    private fun getAppDir(context: Context) =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            context.dataDir.absolutePath
        else {
            val path = context.filesDir.absolutePath
            path.substring(0, path.length - 6)
        }

    /**
     * 数据库路径
     */
    fun getDatabasePath(region: Int) =
        getDatabaseDir() + "/" + when (region) {
            2 -> Constants.DATABASE_NAME_CN
            3 -> Constants.DATABASE_NAME_TW
            else -> Constants.DATABASE_NAME_JP
        }

    /**
     * 数据库压缩包路径
     */
    private fun getDatabaseBrPath(region: Int) =
        getDatabaseDir() + "/" + when (region) {
            2 -> Constants.DATABASE_DOWNLOAD_FILE_NAME_CN
            3 -> Constants.DATABASE_DOWNLOAD_FILE_NAME_TW
            else -> Constants.DATABASE_DOWNLOAD_FILE_NAME_JP
        }


    /**
     * 数据库备份路径
     */
    fun getDatabaseBackupPath(region: Int) =
        getDatabaseDir() + "/" + when (region) {
            2 -> Constants.DATABASE_BACKUP_NAME_CN
            3 -> Constants.DATABASE_BACKUP_NAME_TW
            else -> Constants.DATABASE_BACKUP_NAME_JP
        }

    /**
     * wal 文件路径
     */
    fun getDatabaseWalPath(region: Int) =
        getDatabaseDir() + "/" + when (region) {
            2 -> Constants.DATABASE_WAL_CN
            3 -> Constants.DATABASE_WAL_TW
            else -> Constants.DATABASE_WAL_JP
        }

    /**
     * 备份 wal 文件路径
     */
    fun getDatabaseBackupWalPath(region: Int) =
        getDatabaseDir() + "/" + when (region) {
            2 -> Constants.DATABASE_WAL_BACKUP_CN
            3 -> Constants.DATABASE_WAL_BACKUP_TW
            else -> Constants.DATABASE_WAL_BACKUP_JP
        }

    /**
     * 数据库是否需要判断
     */
    fun needUpdate(type: Int): Boolean {
        val dbFile = File(getDatabasePath(type))
        val dbNotExists = !dbFile.exists()
        val dbSizeError = dbFile.length() < 1 * 1024 * 1024
        return dbNotExists || dbSizeError
    }

    /**
     * 删除数据库文件
     */
    fun deleteBr(type: Int) {
        val db = File(getDatabaseBrPath(type))
        if (db.exists()) {
            db.delete()
        }
    }

    /**
     * 保存文件
     */
    fun save(input: InputStream, output: File) {
        val out = FileOutputStream(output)
        val byte = ByteArray(256)
        var line: Int
        while (input.read(byte).also { line = it } > 0) {
            out.write(byte, 0, line)
        }
        out.flush()
        out.close()
        input.close()
    }

    /**
     * 删除文件
     */
    fun delete(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

}