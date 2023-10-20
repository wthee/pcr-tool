package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.core.content.edit
import androidx.datastore.preferences.core.edit
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.data.model.DatabaseVersion
import cn.wthee.pcrtool.data.network.MyAPIService
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.handler
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.ui.settingSP
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.API_URL
import cn.wthee.pcrtool.utils.Constants.DOWNLOAD_DB_WORK
import cn.wthee.pcrtool.utils.Constants.mediaType
import cn.wthee.pcrtool.workers.DatabaseDownloadWorker
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * 数据库更新
 */
object DatabaseUpdater {

    val sp = settingSP()

    /**
     * 切换版本
     */
    fun changeDatabase(region: RegionType) {
        sp.edit {
            putInt(Constants.SP_DATABASE_TYPE, region.value)
        }
        MainActivity.regionType = region
        handler.sendEmptyMessage(0)
    }

    /**
     * 检查是否需要更新
     * @param fixDb 修复数据库（强制重新下载）
     */
    suspend fun checkDBVersion(fixDb: Boolean = false) {
        //获取数据库最新版本
        try {
            navViewModel.downloadProgress.postValue(-1)
        } catch (_: Exception) {
        }
        try {
            //创建服务
            val service = ApiUtil.create(MyAPIService::class.java, API_URL)
            //接口参数
            val json = JsonObject()
            json.addProperty("regionCode", getRegionCode())

            val body =
                json.toString().toRequestBody(mediaType.toMediaTypeOrNull())

            val version = service.getDbVersion(body)
            //更新判断
            var desc = version.data!!.desc
            if (desc == "") {
                desc = getString(R.string.db_diff_content_none)
            }
            navViewModel.updateDb.postValue(desc)
            downloadDB(version.data!!, fixDb)
        } catch (e: Exception) {
            if (e !is CancellationException) {
                ToastUtil.short(getString(R.string.check_db_error))
            }
            navViewModel.downloadProgress.postValue(-2)
        }
    }

    /**
     * 下载数据库文件
     * @param versionData 版本信息
     */
    @SuppressLint("UnsafeOptInUsageError")
    private fun downloadDB(versionData: DatabaseVersion, fixDb: Boolean) {
        val region = MainActivity.regionType
        val localVersionKey = when (region) {
            RegionType.CN -> Constants.SP_DATABASE_VERSION_CN
            RegionType.TW -> Constants.SP_DATABASE_VERSION_TW
            RegionType.JP -> Constants.SP_DATABASE_VERSION_JP
        }
        val localVersion = sp.getString(localVersionKey, "")
        //数据库文件不存在或有新版本更新时，下载最新数据库文件,切换版本，若文件不存在就更新
        val remoteBackupMode = MyApplication.backupMode
        //正常下载
        val toDownload = localVersion != versionData.toString()  //版本号hash远程不一致
                || (FileUtil.dbNotExists(region) || localVersion == "0")  //数据库wal被清空
                || fixDb

        //下载远程备份
        val toDownloadRemoteBackup =
            remoteBackupMode && File(FileUtil.getDatabaseBackupPath(region)).length() < 1024 * 1024
        if (toDownload || toDownloadRemoteBackup) {
            //远程备份时
            val fileName = when (region) {
                RegionType.CN -> {
                    if (remoteBackupMode) {
                        Constants.DATABASE_DOWNLOAD_FILE_NAME_BACKUP_CN
                    } else {
                        Constants.DATABASE_DOWNLOAD_FILE_NAME_CN
                    }
                }

                RegionType.TW -> {
                    if (remoteBackupMode) {
                        Constants.DATABASE_DOWNLOAD_FILE_NAME_BACKUP_TW
                    } else {
                        Constants.DATABASE_DOWNLOAD_FILE_NAME_TW
                    }
                }

                RegionType.JP -> {
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
                    .putInt(DatabaseDownloadWorker.KEY_REGION, region.value)
                    .build()
                val uploadWorkRequest =
                    OneTimeWorkRequestBuilder<DatabaseDownloadWorker>()
                        .setInputData(data)
                        .build()
                WorkManager.getInstance(MyApplication.context).enqueueUniqueWork(
                    DOWNLOAD_DB_WORK,
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
        }
    }

}

/**
 * 更新本地数据库版本、哈希值
 */
fun updateLocalDataBaseVersion(ver: String) {
    val key = when (MainActivity.regionType) {
        RegionType.CN -> MainPreferencesKeys.SP_DATABASE_VERSION_CN
        RegionType.TW -> MainPreferencesKeys.SP_DATABASE_VERSION_TW
        RegionType.JP -> MainPreferencesKeys.SP_DATABASE_VERSION_JP
    }

    MainScope().launch {
        MyApplication.context.dataStoreMain.edit {
            it[key] = ver
        }
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
        RegionType.CN -> {
            msg = "db error: cn"
            open = {
                openDatabase(AppBasicDatabase.buildDatabase(Constants.DATABASE_NAME_CN).openHelper)
            }
        }

        RegionType.TW -> {
            msg = "db error: tw"
            open = {
                openDatabase(AppBasicDatabase.buildDatabase(Constants.DATABASE_NAME_TW).openHelper)
            }
        }

        RegionType.JP -> {
            msg = "db error: jp"
            open = {
                openDatabase(AppBasicDatabase.buildDatabase(Constants.DATABASE_NAME_JP).openHelper)
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
            ToastUtil.short(getString(R.string.database_remote_backup))
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
