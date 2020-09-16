package cn.wthee.pcrtool.database

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.model.DatabaseVersion
import cn.wthee.pcrtool.data.service.DatabaseService
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.utils.ApiHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.API_URL
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_CHECKED
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_CHECKING
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.ToastUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object DatabaseUpdateHelper {

    private val mContext = MyApplication.getContext()

    //检查是否需要更新
    fun checkDBVersion(notToast: Boolean) {
        //开始
        if (!notToast) {
            ToastUtil.short(NOTICE_TOAST_CHECKING)
        }
        val service = ApiHelper.create(
            DatabaseService::class.java,
            API_URL
        )
        service.getDbVersion().enqueue(object : Callback<DatabaseVersion> {
            override fun onFailure(call: Call<DatabaseVersion>, t: Throwable) {
                CharacterListFragment.handler.sendEmptyMessage(0)
            }

            override fun onResponse(
                call: Call<DatabaseVersion>,
                response: Response<DatabaseVersion>
            ) {
                //更新判断
                try {
                    val version = response.body()!!
                    val databaseVersion = MainActivity.sp.getString(
                        Constants.SP_DATABASE_VERSION,
                        Constants.DATABASE_VERSION
                    ) ?: Constants.DATABASE_VERSION
                    //数据库文件不存在或有新版本更新时，下载最新数据库文件
                    if (FileUtil.needUpadateDb()
                        || databaseVersion == Constants.DATABASE_VERSION
                        || databaseVersion < version.TruthVersion
                    ) {
                        downloadDB(version.TruthVersion)
                        if (!notToast) {
                            ToastUtil.long(Constants.NOTICE_TOAST_TITLE_DB_DOWNLOAD)
                        }
                    } else {
                        if (!notToast) {
                            ToastUtil.short(NOTICE_TOAST_CHECKED)
                        }
                    }
                } catch (e: Exception) {
                    CharacterListFragment.handler.sendEmptyMessage(0)
                }
            }
        })
    }

    //不校验版本，直接下载最新数据库
    fun forceUpdate() {
        downloadDB("0")
    }

    //获取数据库
    private fun downloadDB(ver: String) {
        //开始下载
        val uploadWorkRequest = OneTimeWorkRequestBuilder<DatabaseDownloadWorker>()
            .setInputData(
                Data.Builder()
                    .putString(DatabaseDownloadWorker.KEY_INPUT_URL, API_URL)
                    .putString(DatabaseDownloadWorker.KEY_VERSION, ver)
                    .build()
            )
            .build()
        WorkManager.getInstance(mContext)
            .enqueueUniqueWork("updateDatabase", ExistingWorkPolicy.REPLACE, uploadWorkRequest)

    }

}