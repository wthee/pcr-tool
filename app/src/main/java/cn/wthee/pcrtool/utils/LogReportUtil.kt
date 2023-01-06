package cn.wthee.pcrtool.utils

import android.util.Log
import cn.wthee.pcrtool.BuildConfig
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

//上传日志 fixme Crash 上报
object LogReportUtil {

    fun upload(e: Exception, msg: String) {
        MainScope().launch {
            if (BuildConfig.DEBUG) {
                Log.e("LogReportUtil", msg + e.message)
            }
            CrashReport.postCatchedException(e)
        }
    }
}