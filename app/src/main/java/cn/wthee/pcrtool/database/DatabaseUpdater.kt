package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.datastore.preferences.core.edit
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.data.model.DatabaseVersion
import cn.wthee.pcrtool.data.network.ApiRepository
import cn.wthee.pcrtool.data.preferences.SettingPreferencesKeys
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.handler
import cn.wthee.pcrtool.ui.dataStoreSetting
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.DOWNLOAD_DB_WORK
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.getRegionCode
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.workers.DatabaseDownloadWorker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 数据库更新
 */
object DatabaseUpdater {


    /**
     * 切换版本
     */
    suspend fun changeDatabase(region: RegionType) {
        MyApplication.context.dataStoreSetting.edit {
            it[SettingPreferencesKeys.SP_DATABASE_TYPE] = region.value
            MainActivity.regionType = region
            handler.sendEmptyMessage(0)
        }
    }

    /**
     * 检查是否需要更新
     * @param fixDb 修复数据库（强制重新下载）
     * @param updateDbDownloadState 状态更新
     * @param updateDbVersionText 版本文本更新
     */
    suspend fun checkDBVersion(
        fixDb: Boolean = false,
        updateDbVersionText: (DatabaseVersion?) -> Unit,
        updateDbDownloadState: (Int) -> Unit
    ) {
        //加载中
        updateDbDownloadState(-1)
        //获取远程数据版本
        val version = ApiRepository().getDbVersion(getRegionCode())
        if (version.status == -1) {
            ToastUtil.short(getString(R.string.check_db_error))
            updateDbDownloadState(-2)
            return
        }
        //更新版本文本内容
        updateDbVersionText(version.data)
        //下载数据库
        downloadDB(version.data!!, fixDb, updateDbDownloadState)
    }

    /**
     * 下载数据库文件
     * @param versionData 版本信息
     */
    @SuppressLint("UnsafeOptInUsageError")
    private suspend fun downloadDB(
        versionData: DatabaseVersion,
        fixDb: Boolean,
        updateDbDownloadState: (Int) -> Unit
    ) {
        val region = MainActivity.regionType
        val localVersionKey = when (region) {
            RegionType.CN -> SettingPreferencesKeys.SP_DATABASE_VERSION_CN
            RegionType.TW -> SettingPreferencesKeys.SP_DATABASE_VERSION_TW
            RegionType.JP -> SettingPreferencesKeys.SP_DATABASE_VERSION_JP
        }
        val localVersion = runBlocking {
            MyApplication.context.dataStoreSetting.data.first()[localVersionKey] ?: ""
        }
        //正常下载
        val toDownload = localVersion != versionData.toString()  //版本号hash远程不一致
                || (FileUtil.dbNotExists(region) || localVersion == "0")  //数据库wal被清空
                || fixDb
        if (toDownload) {
            //远程备份时
            val fileName = when (region) {
                RegionType.CN -> Constants.DATABASE_DOWNLOAD_FILE_NAME_CN
                RegionType.TW -> Constants.DATABASE_DOWNLOAD_FILE_NAME_TW
                RegionType.JP -> Constants.DATABASE_DOWNLOAD_FILE_NAME_JP
            }
            //开始下载
            try {
                val data = Data.Builder()
                    .putString(DatabaseDownloadWorker.KEY_VERSION, versionData.toString())
                    .putString(DatabaseDownloadWorker.KEY_FILE, fileName)
                    .putInt(DatabaseDownloadWorker.KEY_REGION, region.value)
                    .build()

                val updateDbRequest =
                    OneTimeWorkRequestBuilder<DatabaseDownloadWorker>()
                        .setInputData(data)
                        .build()
                val workManager = WorkManager.getInstance(MyApplication.context)
                workManager.enqueueUniqueWork(
                    DOWNLOAD_DB_WORK,
                    ExistingWorkPolicy.KEEP,
                    updateDbRequest
                )

                //监听下载进度
                ActivityHelper.instance.currentActivity?.let {
                    workManager.getWorkInfoByIdLiveData(updateDbRequest.id)
                        .observe(it) { workInfo: WorkInfo? ->
                            if (workInfo != null) {
                                when (workInfo.state) {
                                    WorkInfo.State.SUCCEEDED -> {
                                        handler.sendEmptyMessage(region.value)
                                    }

                                    WorkInfo.State.RUNNING -> {
                                        val value =
                                            workInfo.progress.getInt(Constants.KEY_PROGRESS, -1)
                                        updateDbDownloadState(value)
                                    }

                                    WorkInfo.State.FAILED -> {
                                        val value =
                                            workInfo.outputData.getInt(Constants.KEY_PROGRESS, -1)
                                        updateDbDownloadState(value)
                                    }

                                    else -> Unit
                                }
                            }
                        }
                }

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
            updateDbDownloadState(-2)
        }
    }

}

/**
 * 更新本地数据库版本、哈希值
 */
fun updateLocalDataBaseVersion(ver: String) {
    val key = when (MainActivity.regionType) {
        RegionType.CN -> SettingPreferencesKeys.SP_DATABASE_VERSION_CN
        RegionType.TW -> SettingPreferencesKeys.SP_DATABASE_VERSION_TW
        RegionType.JP -> SettingPreferencesKeys.SP_DATABASE_VERSION_JP
    }

    MainScope().launch {
        MyApplication.context.dataStoreSetting.edit {
            it[key] = ver
        }
    }
}

