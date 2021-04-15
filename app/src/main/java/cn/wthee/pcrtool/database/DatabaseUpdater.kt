package cn.wthee.pcrtool.database

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import cn.wthee.pcrtool.MainActivity.Companion.handler
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.DatabaseVersion
import cn.wthee.pcrtool.data.network.service.MyAPIService
import cn.wthee.pcrtool.ui.setting.MainSettingsFragment
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.API_URL
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * 数据库更新
 */
object DatabaseUpdater {

    private lateinit var sp: SharedPreferences

    fun setSp(sp: SharedPreferences) {
        this.sp = sp
    }

    /**
     * 切换版本
     */
    suspend fun changeType() {
        var type = getDatabaseType()
        sp.edit {
            putInt(Constants.SP_DATABASE_TYPE, if (type == 1) 2 else 1)
        }
        checkDBVersion(1)
    }

    /**
     * 检查是否需要更新
     *
     * [from] -1:正常调用  0：点击版本号  1：切换版本调用
     *
     * [force] 是否强制更新
     */
    suspend fun checkDBVersion(from: Int = -1, force: Boolean = false) {
        //TODO 提示开始

        //获取数据库最新版本
        try {
            //创建服务
            val service = ApiUtil.create(
                MyAPIService::class.java,
                API_URL,
                5
            )
            val version = service.getDbVersion(getVersionFileName())
            //更新判断
            downloadDB(version.data!!, from, force)
        } catch (e: Exception) {
            //TODO 提示开始异常
        }
    }

    /**
     * 下载数据库文件
     */
    private fun downloadDB(
        ver: DatabaseVersion,
        from: Int = -1,
        force: Boolean = false
    ) {
        val databaseType = getDatabaseType()
        val localVersion = sp.getString(
            if (databaseType == 1) Constants.SP_DATABASE_VERSION else Constants.SP_DATABASE_VERSION_JP,
            ""
        )
        //数据库文件不存在或有新版本更新时，下载最新数据库文件,切换版本，若文件不存在就更新
        val remoteBackupMode = MyApplication.backupMode
        //正常下载
        val toDownload = localVersion != ver.toString()  //版本号hash远程不一致
                || force
                || (from == -1 && (FileUtil.needUpdate(databaseType) || localVersion == "0"))  //打开应用，数据库wal被清空
                || (from == 1 && !File(FileUtil.getDatabasePath(databaseType)).exists()) //切换数据库时，数据库文件不存在时更新
        //下载远程备份
        val toDownloadRemoteBackup =
            remoteBackupMode && File(FileUtil.getDatabaseBackupPath(databaseType)).length() < 1024 * 1024
        if (toDownload || toDownloadRemoteBackup) {
            //远程备份时
            var fileName = if (databaseType == 1) {
                if (remoteBackupMode) {
                    Constants.DATABASE_DOWNLOAD_FILE_NAME_BACKUP
                } else {
                    Constants.DATABASE_DOWNLOAD_FILE_NAME
                }
            } else {
                if (remoteBackupMode) {
                    Constants.DATABASE_DOWNLOAD_FILE_NAME_BACKUP_JP
                } else {
                    Constants.DATABASE_DOWNLOAD_FILE_NAME_JP
                }
            }
            //强制更新时
            if (force) {
                fileName = if (databaseType == 1) {
                    Constants.DATABASE_DOWNLOAD_FILE_NAME
                } else {
                    Constants.DATABASE_DOWNLOAD_FILE_NAME_JP
                }
            }
            //开始下载
            try {
                val data = Data.Builder()
                    .putString(DatabaseDownloadWorker.KEY_VERSION, ver.toString())
                    .putString(DatabaseDownloadWorker.KEY_FILE, fileName)
                    .putInt(DatabaseDownloadWorker.KEY_VERSION_TYPE, databaseType)
                    .putInt(DatabaseDownloadWorker.KEY_FROM, from)
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
                MainScope().launch {
                    UMCrash.generateCustomLog(e, Constants.EXCEPTION_DOWNLOAD_DB)
                }
            }
        } else {
            //强制更新/切换成功
            if (from != -1) {
                handler.sendEmptyMessage(1)
            }
            //更新数据库版本号
            try {
                MainSettingsFragment.titleDatabase.title =
                    MyApplication.context.getString(R.string.data) + ver.TruthVersion
            } catch (e: Exception) {
            } finally {
                updateLocalDataBaseVersion(ver.toString())
            }
        }
    }

    /**
     * 尝试打开本地数据库
     *
     * 1：正常打开  0：启用备用数据库
     */
    fun tryOpenDatabase(): Int {
        if (getDatabaseType() == 1) {
            try {
                //尝试打开数据库
                if (File(FileUtil.getDatabasePath(1)).exists()) {
                    openDatabase(AppDatabase.buildDatabase(Constants.DATABASE_NAME).openHelper)
                }
            } catch (e: Exception) {
                //启用远程备份数据库
                MainScope().launch {
                    ToastUtil.short(ResourcesUtil.getString(R.string.database_remote_backup))
                    UMCrash.generateCustomLog("OpenDatabaseException", "更新国服数据结构！！！")
                }
                return 0
            }
            //正常打开
            return 1
        } else {
            try {
                //尝试打开数据库
                if (File(FileUtil.getDatabasePath(2)).exists()) {
                    openDatabase(AppDatabaseJP.buildDatabase(Constants.DATABASE_NAME_JP).openHelper)
                }
            } catch (e: Exception) {
                //启用远程备份数据库
                MainScope().launch {
                    ToastUtil.short(ResourcesUtil.getString(R.string.database_remote_backup))
                    UMCrash.generateCustomLog("OpenDatabaseException", "更新日服数据结构！！！")
                }
                return 0
            }
            return 1
        }
    }

    /**
     * 打开数据库
     */
    private fun openDatabase(helper: SupportSQLiteOpenHelper) {
        helper.readableDatabase
        helper.close()
    }

    /**
     * 获取数据库版本
     * 1: 国服 2：日服
     */
    fun getDatabaseType() = sp.getInt(Constants.SP_DATABASE_TYPE, 1)

    /**
     * 获取已选择的游戏版本
     */
    fun getRegion() = if (getDatabaseType() == 1) 2 else {
        //获取查询设置
        val tw = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
            .getBoolean("pvp_region", false)
        if (tw) {
            3
        } else {
            4
        }
    }


    /**
     * 获取数据库文件名
     */
    private fun getVersionFileName() =
        if (getDatabaseType() == 1) Constants.DATABASE_VERSION_URL else Constants.DATABASE_VERSION_URL_JP

    /**
     * 更新本地数据库版本、哈希值
     */
    fun updateLocalDataBaseVersion(ver: String) {
        val type = getDatabaseType()
        sp.edit {
            putString(
                if (type == 1) Constants.SP_DATABASE_VERSION else Constants.SP_DATABASE_VERSION_JP,
                ver
            )
        }
    }

}