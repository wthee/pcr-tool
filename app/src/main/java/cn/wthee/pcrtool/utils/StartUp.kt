package cn.wthee.pcrtool.utils

import android.content.Context
import androidx.startup.Initializer
import com.parse.Parse
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import java.util.*

/**
 * 友盟 SDK 初始化
 */
class UMengInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        UMConfigure.init(
            context,
            PrivateConfig.UMENG_KEY,
            "Umeng",
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }

}


/**
 * Parse SDK 初始化
 */
class ParseInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        //Parse 日志
        Parse.initialize(
            Parse.Configuration.Builder(context)
                .applicationId(PrivateConfig.PARSE_APP_ID)
                .clientKey(PrivateConfig.PARSE_KEY)
                .server(PrivateConfig.PARSE_SERVER)
                .build()
        )
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }

}