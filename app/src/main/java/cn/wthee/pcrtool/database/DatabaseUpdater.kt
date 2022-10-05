package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.core.content.edit
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.DatabaseVersion
import cn.wthee.pcrtool.data.network.MyAPIService
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.handler
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.settingSP
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.API_URL
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * 数据库更新
 */
object DatabaseUpdater {

    val sp = settingSP()

    /**
     * 切换版本
     */
    suspend fun changeDatabase(region: Int) {
        sp.edit {
            putInt(Constants.SP_DATABASE_TYPE, region)
        }
        MainActivity.regionType = region
        checkDBVersion(1)
    }

    /**
     * 检查是否需要更新
     *
     * @param from  -1:正常调用 1：切换版本调用
     */
    suspend fun checkDBVersion(from: Int = -1) {
        //获取数据库最新版本
        try {
            navViewModel.downloadProgress.postValue(-1)
        } catch (_: Exception) {
        }
        try {
            //创建服务
            val service = ApiUtil.create(MyAPIService::class.java, API_URL)
            val version = service.getDbVersion(getVersionFileName())
            //更新判断
            downloadDB(version.data!!, from)
        } catch (e: Exception) {
            if (e !is CancellationException) {
                ToastUtil.short(ResourcesUtil.getString(R.string.check_db_error))
            }
            navViewModel.downloadProgress.postValue(-2)
        }
    }

    /**
     * 下载数据库文件
     * @param versionData 版本信息
     * @param from 调用来源
     */
    @SuppressLint("UnsafeOptInUsageError")
    private fun downloadDB(versionData: DatabaseVersion, from: Int) {
        val region = MainActivity.regionType
        val localVersionKey = when (region) {
            2 -> Constants.SP_DATABASE_VERSION_CN
            3 -> Constants.SP_DATABASE_VERSION_TW
            else -> Constants.SP_DATABASE_VERSION_JP
        }
        val localVersion = sp.getString(localVersionKey, "")
        //数据库文件不存在或有新版本更新时，下载最新数据库文件,切换版本，若文件不存在就更新
        val remoteBackupMode = MyApplication.backupMode
        //正常下载
        val toDownload = localVersion != versionData.toString()  //版本号hash远程不一致
                || (from == -1 && (FileUtil.needUpdate(region) || localVersion == "0"))  //打开应用，数据库wal被清空
                || (from == 1 && !File(FileUtil.getDatabasePath(region)).exists()) //切换数据库时，数据库文件不存在时更新
        //下载远程备份
        val toDownloadRemoteBackup =
            remoteBackupMode && File(FileUtil.getDatabaseBackupPath(region)).length() < 1024 * 1024
        if (toDownload || toDownloadRemoteBackup) {
            //远程备份时
            val fileName = when (region) {
                2 -> {
                    if (remoteBackupMode) {
                        Constants.DATABASE_DOWNLOAD_FILE_NAME_BACKUP_CN
                    } else {
                        Constants.DATABASE_DOWNLOAD_FILE_NAME_CN
                    }
                }
                3 -> {
                    if (remoteBackupMode) {
                        Constants.DATABASE_DOWNLOAD_FILE_NAME_BACKUP_TW
                    } else {
                        Constants.DATABASE_DOWNLOAD_FILE_NAME_TW
                    }
                }
                else -> {
                    if (remoteBackupMode) {
                        Constants.DATABASE_DOWNLOAD_FILE_NAME_BACKUP_JP
                    } else {
                        Constants.DATABASE_DOWNLOAD_FILE_NAME_JP
                    }
                }
            }
            //开始下载
            try {
                val data = Data.Builder()
                    .putString(DatabaseDownloadWorker.KEY_VERSION, versionData.toString())
                    .putString(DatabaseDownloadWorker.KEY_FILE, fileName)
                    .putInt(DatabaseDownloadWorker.KEY_REGION, region)
                    .build()
                val uploadWorkRequest =
                    OneTimeWorkRequestBuilder<DatabaseDownloadWorker>()
                        .setInputData(data)
                        .build()
                WorkManager.getInstance(MyApplication.context).enqueueUniqueWork(
                    "updateDatabase",
                    ExistingWorkPolicy.KEEP,
                    uploadWorkRequest
                )
            } catch (e: Exception) {
                WorkManager.getInstance(MyApplication.context).cancelAllWork()
                LogReportUtil.upload(e, Constants.EXCEPTION_DOWNLOAD_DB)
            }
        } else {
            //更新数据库版本号
            try {
                updateLocalDataBaseVersion(versionData.toString())
            } catch (_: Exception) {
            }
            navViewModel.downloadProgress.postValue(-2)
            //切换成功
            if (from == 1) {
                handler.sendEmptyMessage(region)
            }
        }
    }


    /**
     * 获取数据库文件名
     */
    private fun getVersionFileName() = when (MainActivity.regionType) {
        2 -> Constants.DATABASE_VERSION_URL_CN
        3 -> Constants.DATABASE_VERSION_URL_TW
        else -> Constants.DATABASE_VERSION_URL_JP
    }

}

/**
 * 更新本地数据库版本、哈希值
 */
fun updateLocalDataBaseVersion(ver: String) {
    val sp = settingSP()
    val key = when (MainActivity.regionType) {
        2 -> Constants.SP_DATABASE_VERSION_CN
        3 -> Constants.SP_DATABASE_VERSION_TW
        else -> Constants.SP_DATABASE_VERSION_JP
    }
    sp.edit {
        putString(key, ver)
    }
}

/**
 * 尝试打开本地数据库
 *
 * 1：正常打开  0：启用备用数据库
 */
fun tryOpenDatabase(): Int {
    val msg: String
    val open: () -> Unit
    when (MainActivity.regionType) {
        2 -> {
            msg = "更新国服数据结构！！！"
            open = {
                openDatabase(AppDatabaseCN.buildDatabase(Constants.DATABASE_NAME_CN).openHelper)
            }
        }
        3 -> {
            msg = "更新台服数据结构！！!"
            open = {
                openDatabase(AppDatabaseTW.buildDatabase(Constants.DATABASE_NAME_TW).openHelper)
            }
        }
        else -> {
            msg = "更新日服数据结构！！！"
            open = {
                openDatabase(AppDatabaseJP.buildDatabase(Constants.DATABASE_NAME_JP).openHelper)
            }
        }
    }
    try {
        //尝试打开数据库
        if (File(FileUtil.getDatabasePath(MainActivity.regionType)).exists()) {
            open()
        }
    } catch (e: Exception) {
        //启用远程备份数据库
        MainScope().launch {
            ToastUtil.short(ResourcesUtil.getString(R.string.database_remote_backup))
            LogReportUtil.upload(e, msg)
        }
        return 0
    }
    //正常打开
    return 1
}

/**
 * 打开数据库
 */
fun openDatabase(helper: SupportSQLiteOpenHelper) {
    helper.use {
        it.readableDatabase
    }
}
