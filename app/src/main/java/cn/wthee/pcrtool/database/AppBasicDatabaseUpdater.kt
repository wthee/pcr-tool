package cn.wthee.pcrtool.database

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
import cn.wthee.pcrtool.data.network.apiHttpClient
import cn.wthee.pcrtool.data.preferences.SettingPreferencesKeys
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.handler
import cn.wthee.pcrtool.ui.dataStoreSetting
import cn.wthee.pcrtool.ui.home.DbDownloadState
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.DOWNLOAD_DB_WORK
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.workers.DatabaseDownloadWorker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 数据库更新
 */
object AppBasicDatabaseUpdater {


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
     * @param updateDbVersion 版本文本更新
     */
    suspend fun checkDBVersion(
        fixDb: Boolean = false,
        updateDbVersion: (DatabaseVersion?) -> Unit,
        updateDbDownloadState: (Int) -> Unit
    ) {
        //加载中
        updateDbDownloadState(DbDownloadState.LOADING.state)
        //获取远程数据版本
        val version = ApiRepository(apiHttpClient).getDbVersion(MainActivity.regionType.code)
        if (version.status == -1) {
            ToastUtil.short(getString(R.string.check_db_error))
            updateDbDownloadState(DbDownloadState.NORMAL.state)
            return
        }
        //下载数据库
        downloadDB(version.data!!, fixDb, updateDbVersion, updateDbDownloadState)
    }

    /**
     * 下载数据库文件
     * @param versionData 版本信息
     * @param fixDb 修复数据库（强制重新下载）
     */
    private suspend fun downloadDB(
        versionData: DatabaseVersion,
        fixDb: Boolean,
        updateDbVersion: (DatabaseVersion?) -> Unit,
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

        //版本号hash与远程不一致
        val diffVersion = localVersion != versionData.toString()
        //数据库文件不存在
        val dbNotExist = FileUtil.dbNotExist(region) || localVersion == "0"
        //正常下载
        val toDownload = diffVersion || dbNotExist || fixDb

        if (toDownload) {
            //远程备份时
            val fileName = when (region) {
                RegionType.CN -> Constants.DATABASE_DOWNLOAD_FILE_NAME_CN
                RegionType.TW -> Constants.DATABASE_DOWNLOAD_FILE_NAME_TW
                RegionType.JP -> Constants.DATABASE_DOWNLOAD_FILE_NAME_JP
            }
            //开始下载
            try {
                //强制加载时，删除wal和shm
                if (fixDb) {
                    FileUtil.delete(FileUtil.getDatabasePath(region, Constants.DATABASE_WAL))
                    FileUtil.delete(FileUtil.getDatabasePath(region, Constants.DATABASE_SHM))
                }

                //设置下载文件名
                val data = Data.Builder()
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
                    ExistingWorkPolicy.REPLACE,
                    updateDbRequest,
                )
                //监听下载进度
                ActivityHelper.instance.currentActivity?.let {
                    workManager.getWorkInfoByIdLiveData(updateDbRequest.id)
                        .observe(it) { workInfo: WorkInfo? ->
                            if (workInfo != null) {
                                when (workInfo.state) {

                                    //下载成功，更新版本号，并重新加载
                                    WorkInfo.State.SUCCEEDED -> {
                                        MainScope().launch {
                                            //更新版本文本内容
                                            updateDbVersion(versionData)
                                            handler.sendEmptyMessage(region.value)
                                        }
                                    }

                                    //更新下载进度
                                    WorkInfo.State.RUNNING -> {
                                        val value =
                                            workInfo.progress.getInt(Constants.KEY_PROGRESS, -1)
                                        updateDbDownloadState(value)
                                    }

                                    //下载失败、取消
                                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                                        workManager.cancelUniqueWork(DOWNLOAD_DB_WORK)
                                        updateDbDownloadState(DbDownloadState.NORMAL.state)
                                    }

                                    else -> Unit
                                }
                            }
                        }
                }

            } catch (e: Exception) {
                WorkManager.getInstance(MyApplication.context).cancelUniqueWork(DOWNLOAD_DB_WORK)
                LogReportUtil.upload(e, Constants.EXCEPTION_DOWNLOAD_WORK_DB)
                ToastUtil.short(getString(R.string.db_download_exception))
                updateDbDownloadState(DbDownloadState.NORMAL.state)
            }
        } else {
            //更新数据库版本号
            updateDbVersion(versionData)
            updateDbDownloadState(DbDownloadState.NORMAL.state)
        }
    }

}
