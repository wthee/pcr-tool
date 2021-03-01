package cn.wthee.pcrtool.utils

import android.content.Context
import androidx.startup.Initializer
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.umcrash.UMCrash
import java.util.*

/**
 * 友盟 SDK 初始化
 */
class UMengInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        UMConfigure.init(
            context,
            "5fe591d7adb42d58268e8603",
            "Umeng",
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )
        UMCrash.setDebug(false)
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }

}