package cn.wthee.pcrtool.database

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.handler
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.network.model.DatabaseVersion
import cn.wthee.pcrtool.data.network.service.MyAPIService
import cn.wthee.pcrtool.ui.setting.MainSettingsFragment
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.utils.ApiUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.API_URL
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_CHANGE
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_CHECKING
import cn.wthee.pcrtool.utils.FileUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * 数据库更新
 */
object DatabaseUpdater {

    private val sp: SharedPreferences =
        ActivityHelper.instance.currentActivity!!.getSharedPreferences("main", Context.MODE_PRIVATE)

    /**
     * 检查是否需要更新
     *
     * [fromSetting] -1:正常调用  0：点击版本号  1：切换版本调用
     *
     * [force] 是否强制更新
     */
    fun checkDBVersion(fromSetting: Int = -1, force: Boolean = false) {
        //提示开始
        if (fromSetting == 1) {
            MainActivity.layoutDownload.visibility = View.VISIBLE
            MainActivity.textDownload.text = NOTICE_TOAST_CHANGE
        }
        if (fromSetting == 0) {
            MainActivity.layoutDownload.visibility = View.VISIBLE
            MainActivity.textDownload.text = NOTICE_TOAST_CHECKING
        }
        //获取数据库最新版本
        MainScope().launch {
            try {//创建服务
                val service = ApiUtil.create(
                    MyAPIService::class.java,
                    API_URL
                )
                val version = service.getDbVersion(getVersionFileName())
                //更新判断
                downloadDB(version.data!!, fromSetting, force)
            } catch (e: Exception) {
                handler.sendEmptyMessage(0)
            }
        }
    }

    /**
     * 下载数据库文件
     */
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
        val toDownload = databaseHash != ver.hash  //hash 与远程不一致
                || getLocalDatabaseVersion() != ver.TruthVersion  //版本号与远程不一致
                || force  //强制更新
                || (fromSetting == -1 && (FileUtil.needUpdate(databaseType) || databaseVersion == "0"))  //打开应用，数据库wal被清空
                || (fromSetting == 1 && !File(FileUtil.getDatabasePath(databaseType)).exists()) //切换数据库时，数据库文件不存在时更新
        if (toDownload) {
            //开始下载
            val uploadWorkRequest = OneTimeWorkRequestBuilder<DatabaseDownloadWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(DatabaseDownloadWorker.KEY_VERSION, ver.TruthVersion)
                        .putString(DatabaseDownloadWorker.KEY_HASH, ver.hash)
                        .putInt(DatabaseDownloadWorker.KEY_VERSION_TYPE, databaseType)
                        .build()
                )
                .build()
            WorkManager.getInstance(MyApplication.context).enqueueUniqueWork(
                "updateDatabase",
                ExistingWorkPolicy.KEEP,
                uploadWorkRequest
            )
        } else {
            //强制更新/切换成功，引导关闭应用
            if (fromSetting != -1) {
                handler.sendEmptyMessage(1)
            }
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

    /**
     * 获取数据库版本
     */
    fun getDatabaseType() =
        PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
            .getString("change_database", "1")?.toInt() ?: 1

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
     * 获取本地数据库版本号
     */
    private fun getLocalDatabaseVersion() = sp.getString(
        if (getDatabaseType() == 1) Constants.SP_DATABASE_VERSION else Constants.SP_DATABASE_VERSION_JP,
        "0"
    ) ?: "0"

    /**
     * 获取本地数据库哈希值
     */
    private fun getLocalDatabaseHash() = sp.getString(
        if (getDatabaseType() == 1) Constants.SP_DATABASE_HASH else Constants.SP_DATABASE_HASH_JP,
        "0"
    ) ?: "0"

    /**
     * 获取数据库文件名
     */
    private fun getVersionFileName() =
        if (getDatabaseType() == 1) Constants.DATABASE_VERSION_URL else Constants.DATABASE_VERSION_URL_JP

    /**
     * 更新本地数据库版本、哈希值
     */
    fun updateLocalDataBaseVersion(ver: DatabaseVersion) {
        val sp = ActivityHelper.instance.currentActivity!!.getSharedPreferences(
            "main",
            Context.MODE_PRIVATE
        )
        sp.edit {
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