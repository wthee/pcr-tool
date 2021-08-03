package cn.wthee.pcrtool.utils

import android.content.Context
import android.os.Build
import cn.wthee.pcrtool.BuildConfig
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
    fun getDatabaseDir(context: Context = MyApplication.context) =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            context.dataDir.absolutePath
        else {
            val path = context.filesDir.absolutePath
            path.substring(0, path.length - 6)
        } + "/databases"

    /**
     * 数据库路径
     */
    fun getDatabasePath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_NAME else Constants.DATABASE_NAME_JP

    /**
     * 数据库备份路径
     */
    fun getDatabaseBackupPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_BACKUP_NAME else Constants.DATABASE_BACKUP_NAME_JP

    /**
     * wal 文件路径
     */
    fun getDatabaseWalPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_WAL else Constants.DATABASE_WAL_JP

    /**
     * 备份 wal 文件路径
     */
    fun getDatabaseBackupWalPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_WAL_BACKUP else Constants.DATABASE_WAL_JP_BACKUP

    /**
     * shm 文件路径
     */
    private fun getDatabaseShmPath(type: Int) =
        getDatabaseDir() + "/" + if (type == 1) Constants.DATABASE_SHM else Constants.DATABASE_SHM_JP


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

    /**
     * 获取历史数据库文件列表
     */
    private fun getOldList(context: Context): List<File>? {
        val file = File(getDatabaseDir(context))
        return file.listFiles()?.filter {
            try {
                val code = it.name.split("r")[0].toInt()
                code != BuildConfig.VERSION_CODE
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * 删除历史数据库文件
     */
    fun deleteOldDatabase(context: Context) {
        try {
            getOldList(context)?.forEach {
                it.delete()
            }
        } catch (e: Exception) {

        }
    }
}