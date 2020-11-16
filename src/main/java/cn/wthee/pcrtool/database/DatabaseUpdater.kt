package cn.wthee.pcrtool.database

import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.DatabaseVersion
import cn.wthee.pcrtool.data.service.DatabaseService
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.ui.setting.MainSettingsFragment
import cn.wthee.pcrtool.utils.ApiHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.API_URL
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_CHANGE
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_CHECKING
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_LASTEST
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.ToastUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

object DatabaseUpdater {

    private val mContext = MyApplication.context

    //检查是否需要更新 -1:正常调用 0：点击版本号 1：切换版本调用
    fun checkDBVersion(fromSetting: Int = -1, force: Boolean = false) {
        //获取数据库本地版本
        val databaseType = PreferenceManager.getDefaultSharedPreferences(mContext)
            .getString("change_database", "1")?.toInt() ?: 1
        //提示开始
        if (fromSetting == 1) ToastUtil.short(NOTICE_TOAST_CHANGE)
        if (fromSetting == 0) ToastUtil.short(NOTICE_TOAST_CHECKING)
        //创建服务
        val service = ApiHelper.create(
            DatabaseService::class.java,
            API_URL
        )
        //获取数据库最新版本
        service.getDbVersion(if (databaseType == 1) Constants.DATABASE_VERSION_URL else Constants.DATABASE_VERSION_URL_JP)
            .enqueue(object : Callback<DatabaseVersion> {
                override fun onFailure(call: Call<DatabaseVersion>, t: Throwable) {
                    CharacterListFragment.handler.sendEmptyMessage(0)
                }

                override fun onResponse(
                    call: Call<DatabaseVersion>, response: Response<DatabaseVersion>
                ) {
                    //更新判断
                    val version = response.body()!!.TruthVersion
                    downloadDB(version, databaseType, fromSetting, force)
                }
            })
    }

    //不校验版本，直接下载最新数据库
    fun forceUpdate() {
        val databaseType = PreferenceManager.getDefaultSharedPreferences(mContext)
            .getString("change_database", "1")?.toInt() ?: 1
        downloadDB("0", databaseType)
    }

    //获取数据库
    private fun downloadDB(
        ver: String,
        databaseType: Int,
        fromSetting: Int = -1,
        force: Boolean = false
    ) {
        //更新判断
        try {
            val databaseVersion = MainActivity.sp.getString(
                if (databaseType == 1) Constants.SP_DATABASE_VERSION else Constants.SP_DATABASE_VERSION_JP,
                "0"
            ) ?: "0"
            //数据库文件不存在或有新版本更新时，下载最新数据库文件,切换版本，若文件不存在就更新
            val toDownload = databaseVersion < ver  //有版本更新
                    || force
                    || (fromSetting == -1 && (FileUtil.needUpadateDb(databaseType) || databaseVersion == "0"))  //打开应用，数据库wal被清空
                    || (fromSetting == 1 && !File(FileUtil.getDatabasePath(databaseType)).exists()) //切换数据库时，切换至的版本，文件不存在时更新
            if (toDownload) {
                ToastUtil.long(Constants.NOTICE_TOAST_TITLE_DB_DOWNLOAD)
                //开始下载
                val uploadWorkRequest = OneTimeWorkRequestBuilder<DatabaseDownloadWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(DatabaseDownloadWorker.KEY_INPUT_URL, API_URL)
                            .putString(DatabaseDownloadWorker.KEY_VERSION, ver)
                            .putInt(DatabaseDownloadWorker.KEY_VERSION_TYPE, databaseType)
                            .putInt(DatabaseDownloadWorker.KEY_FROM_SETTING, fromSetting)
                            .build()
                    )
                    .build()
                WorkManager.getInstance(mContext)
                    .enqueueUniqueWork(
                        "updateDatabase",
                        ExistingWorkPolicy.REPLACE,
                        uploadWorkRequest
                    )
            } else {
                if (fromSetting == 1) {
                    CharacterListFragment.handler.sendEmptyMessage(2)
                }
                if (fromSetting == 0) ToastUtil.short(NOTICE_TOAST_LASTEST)
                //更新数据库版本号
                try {
                    MainSettingsFragment.titleDatabase.title =
                        MyApplication.context.getString(R.string.data) + ver
                } catch (e: Exception) {
                } finally {
                    MainActivity.sp.edit {
                        putString(
                            if (databaseType == 1)
                                Constants.SP_DATABASE_VERSION
                            else
                                Constants.SP_DATABASE_VERSION_JP,
                            ver
                        )
                    }
                }
            }
        } catch (e: Exception) {
            CharacterListFragment.handler.sendEmptyMessage(0)
        }
    }

}