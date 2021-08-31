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

    val toBackupFileNames = arrayListOf(
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

    private fun getBackupFiles(context: Context): ArrayList<String> {
        val dbPath = getDatabaseDir(context)

        return arrayListOf(
            "$dbPath/pvp.db",
            "$dbPath/pvp.db-shm",
            "$dbPath/pvp.db-wal",
            "${getAppDir(context) + "/shared_prefs"}/main.xml"
        )
    }

    /**
     * 导出收藏等数据
     */
    fun exportUserFile(context: Context) {
        try {
            AppPvpDatabase.getInstance().close()
            val files = getBackupFiles(context)
            val externalPath = context.getExternalFilesDir("")!!.path + "/pcr-tool"
            val folder = File(externalPath)
            if (!folder.exists()) {
                folder.mkdir()
            }
            var msg = ""
            var error = ""
            files.forEachIndexed { index, path ->
                val saved = FileSaveHelper(context).saveFile(File(path), toBackupFileNames[index])
                if (saved) {
                    msg += toBackupFileNames[index] + "\n"
                } else {
                    error += toBackupFileNames[index] + "\n"
                }
            }
            ToastUtil.long(
                if (msg != "") {
                    "备份成功：\n$msg"
                } else {
                    ""
                } + if (error != "") {
                    "\n\n备份失败：\n$error"
                } else {
                    ""
                }
            )
        } catch (e: Exception) {
            UMengLogUtil.upload(e, Constants.EXCEPTION_DATA_EXPORT)
            ToastUtil.short(Constants.EXCEPTION_DATA_EXPORT)
        }
    }

    /**
     * 导入收藏等数据
     */
    fun importUserFile(context: Context) {
        try {
            AppPvpDatabase.getInstance().close()
            val files = getBackupFiles(context)
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
            var msg = ""
            var error = ""
            pvpImportFiles.forEachIndexed { index, path ->
                try {
                    File(path).copyTo(File(files[index]), true)
                    msg += toBackupFileNames[index] + "\n"
                } catch (e: Exception) {
                    error += toBackupFileNames[index] + "\n"
                }
            }
            ToastUtil.long(
                if (msg != "") {
                    "加载成功：\n$msg"
                } else {
                    ""
                } + if (error != "") {
                    "\n\n加载失败：\n$error"
                } else {
                    ""
                }
            )
        } catch (e: Exception) {
            UMengLogUtil.upload(e, Constants.EXCEPTION_DATA_IMPORT)
            ToastUtil.short(Constants.EXCEPTION_DATA_IMPORT)
        }
    }
}