package cn.wthee.pcrtool.utils

import android.content.Context
import android.os.Build
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.database.AppPvpDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 文件路径获取
 */
object FileUtil {

    private val toBackupFileNames = arrayListOf(
        "pvp.db",
        "pvp.db-shm",
        "pvp.db-wal",
        "main.xml"
    )


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
     * shm 文件路径
     */
    private fun getDatabaseShmPath(region: Int) =
        getDatabaseDir() + "/" + when (region) {
            2 -> Constants.DATABASE_SHM_CN
            3 -> Constants.DATABASE_SHM_TW
            else -> Constants.DATABASE_SHM_JP
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

    private fun getNeedBackupFiles(context: Context): ArrayList<String> {
        val dbPath = getDatabaseDir(context)

        return arrayListOf(
            "$dbPath/pvp.db",
            "$dbPath/pvp.db-shm",
            "$dbPath/pvp.db-wal",
            "${getAppDir(context)}/shared_prefs/main.xml"
        )
    }

    /**
     * 导出收藏等数据
     */
    fun exportUserFile(context: Context) {
        try {
            AppPvpDatabase.getInstance().close()
            val files = getNeedBackupFiles(context)
            val externalPath = context.getExternalFilesDir("")!!.path + "/pcr-tool"
            val folder = File(externalPath)
            if (!folder.exists()) {
                folder.mkdir()
            }
            var success = 0
            var error = 0
            var count = 0
            files.forEachIndexed { index, path ->
                if (File(path).exists()) {
                    val saved =
                        FileSaveHelper(context).saveFile(File(path), toBackupFileNames[index])
                    count++
                    if (saved) {
                        success++
                    } else {
                        error++
                    }
                }
            }
            if (count > 0) {
                if (success > 0) {
                    ToastUtil.short("导出成功：$success/$count")
                } else {
                    ToastUtil.short("导出失败：$error/$count")
                }
            } else {
                ToastUtil.short("没有找到可导出的文件")
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, Constants.EXCEPTION_DATA_EXPORT)
            ToastUtil.short(Constants.EXCEPTION_DATA_EXPORT)
        }
    }

    /**
     * 导入收藏等数据
     */
    fun importUserFile(context: Context): Boolean {
        try {
            AppPvpDatabase.getInstance().close()
            val files = getNeedBackupFiles(context)
            val externalPath = FileSaveHelper.getDocPath()
            val pvpImportFiles = arrayListOf(
                "$externalPath/pvp.db",
                "$externalPath/pvp.db-shm",
                "$externalPath/pvp.db-wal",
                "$externalPath/main.xml",
            )
            val folder = File(externalPath)
            if (!folder.exists()) {
                folder.mkdir()
            }
            var success = 0
            var error = 0
            var count = 0
            pvpImportFiles.forEachIndexed { index, path ->
                if (File(path).exists()) {
                    val saved = FileSaveHelper(context).readFile(File(files[index]))
                    count++
                    if (saved) {
                        success++
                    } else {
                        error++
                    }
                }
            }
            if (count > 0) {
                if (success > 0) {
                    return true
                } else {
                    ToastUtil.short("导入失败：$error/$count")
                }
            } else {
                ToastUtil.short("没有找到可导入的文件")
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, Constants.EXCEPTION_DATA_IMPORT)
            ToastUtil.short(Constants.EXCEPTION_DATA_IMPORT)
        }
        return false
    }
}