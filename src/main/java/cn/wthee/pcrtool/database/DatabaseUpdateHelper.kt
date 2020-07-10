package cn.wthee.pcrtool.database

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.*
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.service.DatabaseService
import cn.wthee.pcrtool.utils.ApiHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.API_URL
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.ToastUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class DatabaseUpdateHelper {

    private val mContext = MyApplication.getContext()

    //检查是否需要更新
    fun checkDBVersion(lifecycleOwner: LifecycleOwner) {

        val service = ApiHelper.create(
            DatabaseService::class.java,
            API_URL
        )
        CoroutineScope(IO).launch {
            val version = service.getDbVersion()
            //更新判断
            if (FileUtil.needUpadateDb() || MainActivity.databaseVersion == null || version.TruthVersion > MainActivity.databaseVersion!!) {
                downloadDB(version.TruthVersion, lifecycleOwner)
                Looper.prepare()
                ToastUtil.long(Constants.NOTICE_TOAST_TITLE)
                Looper.loop()
            }
        }
    }

    //获取数据库
    private fun downloadDB(ver: String, lifecycleOwner: LifecycleOwner) {
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

        Handler(Looper.getMainLooper()).post {
            WorkManager.getInstance(mContext).getWorkInfoByIdLiveData(uploadWorkRequest.id)
                .observe(lifecycleOwner,
                    Observer<WorkInfo?> { workInfo ->
                        if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                            ToastUtil.short("数据库更新完成~")
                        }
                    })

        }
    }

}