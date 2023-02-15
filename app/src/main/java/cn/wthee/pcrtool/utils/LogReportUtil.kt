package cn.wthee.pcrtool.utils

import android.util.Log
import cn.wthee.pcrtool.BuildConfig
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Bugly 上传日志
 */
object LogReportUtil {

    /**
     * 上传
     */
    fun upload(e: Exception, msg: String) {
        MainScope().launch {
            if (BuildConfig.DEBUG) {
                Log.e("LogReportUtil", msg + e.message)
            }
            //排除协程取消异常、SQLite异常后上传
            if (e !is CancellationException && !(e.message?:"").contains("no such table")) {
                val exception = Exception("$msg\n${e.message}")
                exception.stackTrace = e.stackTrace
                CrashReport.postCatchedException(exception)
            }
        }
    }
}