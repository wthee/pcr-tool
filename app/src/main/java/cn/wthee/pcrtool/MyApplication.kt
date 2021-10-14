package cn.wthee.pcrtool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import cn.wthee.pcrtool.database.tryOpenDatabase
import cn.wthee.pcrtool.utils.ApiUtil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
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
        if (!BuildConfig.DEBUG) {
            backupMode = tryOpenDatabase() == 0
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .componentRegistry {
                if (SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder(context))
                } else {
                    add(GifDecoder())
                }
            }
            .allowHardware(false)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .availableMemoryPercentage(0.5)
            .okHttpClient {
                ApiUtil.getClient(60)
            }
            .build()
    }

}
