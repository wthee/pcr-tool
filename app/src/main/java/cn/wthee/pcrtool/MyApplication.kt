package cn.wthee.pcrtool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import cn.wthee.pcrtool.database.tryOpenDatabase
import cn.wthee.pcrtool.utils.ApiUtil
import cn.wthee.pcrtool.utils.BuglyInitializer
import coil.ComponentRegistry
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.memory.MemoryCache
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
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        //Bugly
        BuglyInitializer().create(this)
        //数据库
        if (!BuildConfig.DEBUG) {
            backupMode = tryOpenDatabase() == 0
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .components(
                ComponentRegistry().newBuilder()
                    .add(
                        if (SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoderDecoder.Factory()
                        } else {
                            GifDecoder.Factory()
                        }
                    )
                    .build()
            )
            .allowHardware(false)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .memoryCache(MemoryCache.Builder(context).maxSizePercent(0.5).build())
            .okHttpClient {
                ApiUtil.getClient(60)
            }
            .build()
    }
}
