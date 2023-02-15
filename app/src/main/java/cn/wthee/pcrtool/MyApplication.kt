package cn.wthee.pcrtool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import cn.wthee.pcrtool.database.tryOpenDatabase
import cn.wthee.pcrtool.utils.ApiUtil
import cn.wthee.pcrtool.utils.BuglyInitializer
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.FileUtil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.request.CachePolicy
import dagger.hilt.android.HiltAndroidApp


/**
 * 应用初始
 */
@HiltAndroidApp
class MyApplication : Application(), ImageLoaderFactory {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var backupMode = false
        var URL_DOMAIN = "wthee.xyz"
        var useIpOnFlag = false

    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        useIpOnFlag = context.getSharedPreferences("setting", Context.MODE_PRIVATE)!!
            .getBoolean(Constants.SP_USE_IP, false)
        //使用ip访问
        if (useIpOnFlag) {
            URL_DOMAIN = "96.45.190.76"
        }
        Log.e("DEBUG", URL_DOMAIN)
        //Bugly
        BuglyInitializer().create(this)
        //数据库
        if (!BuildConfig.DEBUG) {
            backupMode = tryOpenDatabase() == 0
        }

        //清除coil旧的缓存文件
        FileUtil.delete(context.cacheDir.resolve("image_cache"), false)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .allowHardware(false)
            //禁用内存缓存
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCache {
                //调整缓存位置，避免缓存被系统自动清除
                DiskCache.Builder()
                    .directory(context.filesDir.resolve(Constants.COIL_DIR))
                    .build()
            }
            //禁用后，优先从本地缓存STANDARD_MEMORY_MULTIPLIER
            .respectCacheHeaders(false)
            .okHttpClient {
                ApiUtil.buildClient()
            }
            .build()
    }
}
