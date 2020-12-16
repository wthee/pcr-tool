package cn.wthee.pcrtool.database

import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.network.model.DatabaseVersion
import cn.wthee.pcrtool.data.network.service.MyAPIService
import cn.wthee.pcrtool.ui.home.MainPagerFragment
import cn.wthee.pcrtool.ui.setting.MainSettingsFragment
import cn.wthee.pcrtool.utils.ApiHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.API_URL
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_CHANGE
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_CHECKING
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_LASTEST
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.ToastUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File


object DatabaseUpdater {

    //检查是否需要更新 -1:正常调用  0：点击版本号  1：切换版本调用
    fun checkDBVersion(fromSetting: Int = -1, force: Boolean = false) {
        //提示开始
        if (fromSetting == 1) ToastUtil.short(NOTICE_TOAST_CHANGE)
        if (fromSetting == 0) ToastUtil.short(NOTICE_TOAST_CHECKING)
        //获取数据库最新版本
        MainScope().launch {
            try {//创建服务
                val service = ApiHelper.create(
                    MyAPIService::class.java,
                    API_URL
                )
                val version = service.getDbVersion(getVersionFileName())
                //更新判断
                downloadDB(version.data!!, fromSetting, force)
            } catch (e: Exception) {
                Log.e("error", e.message ?: "")
                MainPagerFragment.handler.sendEmptyMessage(0)
            }
        }
    }

    //不校验版本，直接下载最新数据库
    fun forceUpdate() {
        try {
            //更新判断
            downloadDB(DatabaseVersion("0", "hash"), force = true)
        } catch (e: Exception) {
            MainPagerFragment.handler.sendEmptyMessage(0)
        }
    }

    //获取数据库
    private fun downloadDB(
        ver: DatabaseVersion,
        fromSetting: Int = -1,
        force: Boolean = false
    ) {
        //更新判断
        val databaseVersion = getLocalDatabaseVersion()
        val databaseHash = getLocalDatabaseHash()
        val databaseType = getDatabaseType()
        //数据库文件不存在或有新版本更新时，下载最新数据库文件,切换版本，若文件不存在就更新
        val toDownload = databaseHash != ver.hash  //有版本更新
                || getLocalDatabaseVersion() != ver.TruthVersion
                || force
                || (fromSetting == -1 && (FileUtil.needUpdate(databaseType) || databaseVersion == "0"))  //打开应用，数据库wal被清空
                || (fromSetting == 1 && !File(FileUtil.getDatabasePath(databaseType)).exists()) //切换数据库时，切换至的版本，文件不存在时更新
        if (toDownload) {
            ToastUtil.long(Constants.NOTICE_TOAST_TITLE_DB_DOWNLOAD)
            //开始下载
            val uploadWorkRequest = OneTimeWorkRequestBuilder<DatabaseDownloadWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(DatabaseDownloadWorker.KEY_VERSION, ver.TruthVersion)
                        .putString(DatabaseDownloadWorker.KEY_HASH, ver.hash)
                        .putInt(DatabaseDownloadWorker.KEY_VERSION_TYPE, databaseType)
                        .putInt(DatabaseDownloadWorker.KEY_FROM_SETTING, fromSetting)
                        .build()
                )
                .build()
            WorkManager.getInstance(MyApplication.context).enqueueUniqueWork(
                "updateDatabase",
                ExistingWorkPolicy.REPLACE,
                uploadWorkRequest
            )
        } else {
            //切换成功
            if (fromSetting == 1) MainPagerFragment.handler.sendEmptyMessage(2)
            //无需更新
            if (fromSetting == 0) ToastUtil.short(NOTICE_TOAST_LASTEST)
            //更新数据库版本号
            try {
                MainSettingsFragment.titleDatabase.title =
                    MyApplication.context.getString(R.string.data) + ver.TruthVersion
            } catch (e: Exception) {
            } finally {
                updateLocalDataBaseVersion(ver)
            }
        }
    }


    private fun getDatabaseType() =
        PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
            .getString("change_database", "1")?.toInt() ?: 1


    private fun getLocalDatabaseVersion() = MainActivity.sp.getString(
        if (getDatabaseType() == 1) Constants.SP_DATABASE_VERSION else Constants.SP_DATABASE_VERSION_JP,
        "0"
    ) ?: "0"

    private fun getLocalDatabaseHash() = MainActivity.sp.getString(
        if (getDatabaseType() == 1) Constants.SP_DATABASE_HASH else Constants.SP_DATABASE_HASH_JP,
        "0"
    ) ?: "0"

    private fun getVersionFileName() =
        if (getDatabaseType() == 1) Constants.DATABASE_VERSION_URL else Constants.DATABASE_VERSION_URL_JP

    fun updateLocalDataBaseVersion(ver: DatabaseVersion) {
        MainActivity.sp.edit {
            val type = getDatabaseType()
            putString(
                if (type == 1)
                    Constants.SP_DATABASE_VERSION
                else
                    Constants.SP_DATABASE_VERSION_JP,
                ver.TruthVersion
            )
            putString(
                if (type == 1)
                    Constants.SP_DATABASE_HASH
                else
                    Constants.SP_DATABASE_HASH_JP,
                ver.hash
            )
        }
    }

}