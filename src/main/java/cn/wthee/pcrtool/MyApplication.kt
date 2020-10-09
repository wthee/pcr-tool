package cn.wthee.pcrtool

import android.app.Application
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import cn.wthee.pcrtool.utils.Logger
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy
import coil.util.CoilUtils
import okhttp3.OkHttpClient

class MyApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        //获取Context
        context = applicationContext
        Logger.init(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .componentRegistry {
                if (SDK_INT >= 28) {
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
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(context))
                    .build()
            }
            .build()
    }

    companion object {
        private lateinit var context: Context
        fun getContext() = context
    }
}