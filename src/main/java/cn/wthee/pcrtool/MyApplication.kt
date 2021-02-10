package cn.wthee.pcrtool

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.utils.ApiUtil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy

/**
 * 应用初始
 */
class MyApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        backupMode = DatabaseUpdater.tryOpenDatabase() == 0
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .componentRegistry {
                if (SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder())
                } else {
                    add(GifDecoder())
                }
            }
            .crossfade(true)
            .allowHardware(false)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .availableMemoryPercentage(0.75)
            .okHttpClient {
                ApiUtil.getClient(60)
            }
            .build()
    }

    companion object {
        lateinit var context: Context
        var backupMode = false
    }


}