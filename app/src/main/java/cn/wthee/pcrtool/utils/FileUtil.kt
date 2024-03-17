package cn.wthee.pcrtool.utils

import android.content.Context
import android.os.Build
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.utils.Constants.COIL_DIR
import cn.wthee.pcrtool.utils.Constants.VIDEO_DIR
import java.io.File

/**
 * 文件路径获取
 */
object FileUtil {

    /**
     * 数据库所在文件夹
     */
    fun getDatabaseDir(context: Context = MyApplication.context) = getAppDir(context) + "/databases"

    /**
     * 其他文件下载所在文件夹
     */
    fun getDownloadDir(context: Context = MyApplication.context) = getAppDir(context) + "/download"

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
    private fun getDatabasePath(region: RegionType) =
        getDatabaseDir() + "/" + when (region) {
            RegionType.CN -> Constants.DATABASE_NAME_CN
            RegionType.TW -> Constants.DATABASE_NAME_TW
            RegionType.JP -> Constants.DATABASE_NAME_JP
        }

    /**
     * 数据库压缩包路径
     */
    private fun getDatabaseBrPath(region: RegionType) =
        getDatabaseDir() + "/" + when (region) {
            RegionType.CN -> Constants.DATABASE_DOWNLOAD_FILE_NAME_CN
            RegionType.TW -> Constants.DATABASE_DOWNLOAD_FILE_NAME_TW
            RegionType.JP -> Constants.DATABASE_DOWNLOAD_FILE_NAME_JP
        }

    /**
     * wal 文件路径
     */
    fun getDatabaseWalPath(region: RegionType) =
        getDatabaseDir() + "/" + when (region) {
            RegionType.CN -> Constants.DATABASE_NAME_CN
            RegionType.TW -> Constants.DATABASE_NAME_TW
            RegionType.JP -> Constants.DATABASE_NAME_JP
        } + "-wal"

    /**
     * shm 文件路径
     */
    fun getDatabaseShmPath(region: RegionType) =
        getDatabaseDir() + "/" + when (region) {
            RegionType.CN -> Constants.DATABASE_NAME_CN
            RegionType.TW -> Constants.DATABASE_NAME_TW
            RegionType.JP -> Constants.DATABASE_NAME_JP
        } + "-shm"

    /**
     * 数据库是否需要判断
     */
    fun dbNotExists(type: RegionType): Boolean {
        val dbFile = File(getDatabasePath(type))
        return !dbFile.exists()
    }

    /**
     * 删除数据库文件
     */
    fun deleteBr(type: RegionType) {
        val db = File(getDatabaseBrPath(type))
        if (db.exists()) {
            db.delete()
        }
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
     * 删除文件及文件夹
     * @param saveDir
     */
    fun delete(file: File, saveDir: Boolean = true) {
        if (file.isDirectory) {
            val files = file.listFiles()
            files?.forEach {
                delete(it)
            }
            if (!saveDir) {
                //删除文件夹
                file.delete()
            }
        } else if (file.exists()) {
            file.delete()
        }
    }

    /**
     * 获取文件夹内文件大小
     *
     * @return 大小、文件数
     */
    fun getMediaDirSize(context: Context): Pair<String, Int> {
        val dir = context.filesDir.resolve(COIL_DIR)
        val videoDir = context.filesDir.resolve(VIDEO_DIR)
        var size = 0L
        var imageCount = 0
        var videoCount = 0
        dir.listFiles()?.forEach {
            size += it.length()
            imageCount++
        }
        videoDir.listFiles()?.forEach {
            it.listFiles()?.forEach { videoCache ->
                size += videoCache.length()
                videoCount++
            }
        }
        return Pair(size.convertFileSize(), imageCount / 2 + videoCount)
    }

    /**
     * 格式化文件大小格式
     */
    private fun Long.convertFileSize(): String {
        val kb: Long = 1024
        val mb = kb * 1024
        val gb = mb * 1024
        return when {
            this >= gb -> {
                String.format("%.1f GB", this.toFloat() / gb)
            }

            this >= mb -> {
                val f = this.toFloat() / mb
                String.format(if (f > 100) "%.0f MB" else "%.1f MB", f)
            }

            this >= kb -> {
                val f = this.toFloat() / kb
                String.format(if (f > 100) "%.0f KB" else "%.1f KB", f)
            }

            else -> String.format("%d B", this)
        }
    }
}