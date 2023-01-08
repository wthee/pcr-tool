package cn.wthee.pcrtool.utils

import android.content.Context
import androidx.startup.Initializer
import cn.wthee.pcrtool.BuildConfig
import com.tencent.bugly.crashreport.CrashReport
import java.util.*

/**
 * Bugly SDK 初始化
 */
class BuglyInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        CrashReport.initCrashReport(context, PrivateConfig.BUGLY_KEY, BuildConfig.DEBUG)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }

}