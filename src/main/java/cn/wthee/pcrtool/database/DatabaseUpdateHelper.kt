package cn.wthee.pcrtool.database

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.model.DatabaseVersion
import cn.wthee.pcrtool.data.service.DatabaseService
import cn.wthee.pcrtool.utils.ApiHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.API_URL
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_CHECKED
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_CHECKING
import cn.wthee.pcrtool.utils.Constants.NOTICE_TOAST_TIMEOUT
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.ToastUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DatabaseUpdateHelper {

    private val mContext = MyApplication.getContext()

    companion object {
        var downloading = true
    }

    //检查是否需要更新
    fun checkDBVersion() {
        //开始
        ToastUtil.short(NOTICE_TOAST_CHECKING)
        val service = ApiHelper.create(
            DatabaseService::class.java,
            API_URL
        )
        service.getDbVersion().enqueue(object : Callback<DatabaseVersion> {
            override fun onFailure(call: Call<DatabaseVersion>, t: Throwable) {
                ToastUtil.short(NOTICE_TOAST_TIMEOUT)
            }

            override fun onResponse(
                call: Call<DatabaseVersion>,
                response: Response<DatabaseVersion>
            ) {
                //更新判断
                val version = response.body()!!
                if (FileUtil.needUpadateDb() || MainActivity.databaseVersion == null || version.TruthVersion > MainActivity.databaseVersion!!) {
                    downloadDB(version.TruthVersion)
                    ToastUtil.long(Constants.NOTICE_TOAST_TITLE)
                } else {
                    ToastUtil.short(NOTICE_TOAST_CHECKED)
                }
            }
        })
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